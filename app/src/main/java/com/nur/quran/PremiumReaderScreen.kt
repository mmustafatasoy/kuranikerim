package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Reference design: 4 original image panels (list in ReferenceSurahList,
 * colorful Mushaf with fixed player, Nâs verse tracking, bottom settings sheet).
 * Only verified ayat/meal/audio are displayed; no fabricated word timestamps.
 */
@Composable
fun PremiumReaderScreen(
    surah: Surah,
    globalAyahOffset: Int,
    store: ProgressStore,
    fontSize: Int,
    onFontSize: (Int) -> Unit,
    onBack: () -> Unit,
    onWorship: () -> Unit = onBack
) {
    val ctx=LocalContext.current
    val last=remember(surah.number) {
        store.position().takeIf { it.surah==surah.number }?.ayah
            ?.coerceIn(1,surah.ayahCount) ?: 1
    }
    val scroll=rememberLazyListState(initialFirstVisibleItemIndex=last-1)
    var settings by remember(surah.number) {
        mutableStateOf(store.loadRefSettings().copy(arabicSize=fontSize))
    }
    var settingsOpen by remember { mutableStateOf(false) }
    var localPanel by remember { mutableStateOf(false) }
    var playingVerse by remember(surah.number) { mutableIntStateOf(0) }
    var isPlaying by remember(surah.number) { mutableStateOf(false) }
    var requestedVerse by remember(surah.number) { mutableIntStateOf(last) }
    var playNonce by remember(surah.number) { mutableIntStateOf(0) }
    var savedRevision by remember(surah.number) { mutableIntStateOf(0) }
    var meal by remember(surah.number) { mutableStateOf<List<String>?>(null) }
    var mealError by remember(surah.number) { mutableStateOf(false) }
    var mealRetry by remember(surah.number) { mutableIntStateOf(0) }
    var searchOpen by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var bookmarkedTarget by remember(surah.number) { mutableIntStateOf(0) }

    fun saveSettings(value:RefReadingSettings) {
        settings=value
        store.persistRefSettings(value)
        if(value.arabicSize != fontSize) onFontSize(value.arabicSize)
    }

    LaunchedEffect(surah.number,mealRetry) {
        mealError=false
        try { meal=TurkishMealRepository(ctx.applicationContext)
            .surah(surah.number,surah.ayahCount) }
        catch(_:Exception){mealError=true}
    }
    LaunchedEffect(surah.number,scroll) {
        snapshotFlow { scroll.firstVisibleItemIndex }
            .distinctUntilChanged().collect {index->
                store.savePosition(surah.number,(index+1).coerceIn(1,surah.ayahCount))
            }
    }
    LaunchedEffect(playingVerse,isPlaying) {
        if(isPlaying && playingVerse in 1..surah.ayahCount)
            scroll.animateScrollToItem(playingVerse-1)
    }
    LaunchedEffect(bookmarkedTarget) {
        if(bookmarkedTarget in 1..surah.ayahCount)
            scroll.animateScrollToItem(bookmarkedTarget-1)
    }
    Column(Modifier.fillMaxSize().background(RefPaper)) {
        RefTopBar(
            surah.turkishName.uppercase()+" SURESİ",
            surah.number.toString()+". Sure • "+surah.ayahCount+
                " Ayet • "+if(surah.revelation.equals("Medinan",true))"Medine" else "Mekke",
            onBack=onBack,
            onBookmark={
                val n=(scroll.firstVisibleItemIndex+1).coerceIn(1,surah.ayahCount)
                store.toggleBookmark(surah.number,n);savedRevision++
            },
            onSettings={settingsOpen=true}
        )
        RefSurahBanner(surah)
        if (settings.mealVisible) {
            Text(
                "Türkçe meal: Şaban Piriş • QuranEnc.com",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 1.dp),
                fontSize = 10.sp, color = RefGreen, textAlign = TextAlign.End
            )
        }
        if(searchOpen) {
            Row(Modifier.fillMaxWidth().padding(horizontal=9.dp),
                verticalAlignment=Alignment.CenterVertically) {
                OutlinedTextField(search,{search=it},singleLine=true,
                    modifier=Modifier.weight(1f),
                    placeholder={Text("Bu surede ayet ara",fontSize=12.sp)})
                IconButton(onClick={
                    val index=surah.verses.indexOfFirst { it.contains(search,true) }
                    if(index>=0)bookmarkedTarget=index+1
                }) { Icon(Icons.Default.Search,"Bul",tint=RefGreen) }
                IconButton(onClick={searchOpen=false;search=""}) {
                    Icon(Icons.Default.Close,"Kapat")
                }
            }
        }
        if(settings.mealVisible && meal==null) {
            Row(Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=2.dp),
                verticalAlignment=Alignment.CenterVertically) {
                if(mealError) {
                    Text("Meal yüklenemedi. İnterneti kontrol et.",color=RefRed,fontSize=11.sp,
                        modifier=Modifier.weight(1f))
                    TextButton(onClick={mealRetry++}){Text("Yenile")}
                } else {
                    CircularProgressIndicator(Modifier.size(14.dp),strokeWidth=2.dp)
                    Spacer(Modifier.width(7.dp))
                    Text("Türkçe meal yükleniyor…",fontSize=11.sp,color=RefGreen)
                }
            }
        }
        LazyColumn(
            state=scroll, modifier=Modifier.weight(1f),
            contentPadding=PaddingValues(horizontal=9.dp,vertical=7.dp),
            verticalArrangement=Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(surah.verses,key={i,_->i}) { index,ayahText ->
                val n=index+1
                val active=isPlaying && playingVerse==n
                val bookmark=remember(savedRevision,n) {store.hasBookmark(surah.number,n)}
                Surface(
                    modifier=Modifier.fillMaxWidth(),
                    color=if(active)Color(0xFFFFEBE7) else Color.White,
                    shape=RoundedCornerShape(15.dp),
                    border=if(active)BorderStroke(1.dp,Color(0xFFFF9F99)) else null
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal=6.dp,vertical=7.dp),
                        verticalArrangement=Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            RefBadge(n,active,Modifier.size(41.dp))
                            Spacer(Modifier.weight(1f))
                            if(active) Text("● OKUNUYOR",color=RefRed,
                                fontWeight=FontWeight.Bold,fontSize=10.sp)
                            IconButton(onClick={requestedVerse=n;playNonce++},
                                modifier=Modifier.size(42.dp)) {
                                Icon(if(active)Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    "Ayet "+n+" oynat",modifier=Modifier.size(34.dp),
                                    tint=if(active)RefRed else RefGreen)
                            }
                            IconButton(onClick={
                                store.toggleBookmark(surah.number,n);savedRevision++
                            }, modifier=Modifier.size(37.dp)) {
                                Icon(if(bookmark)Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    "Yer imi",tint=if(bookmark)RefGold else RefGreen,
                                    modifier=Modifier.size(20.dp))
                            }
                        }
                        RefColoredArabic(ayahText,active,settings)
                        if(settings.mealVisible) {
                            meal?.getOrNull(index)?.let { translation ->
                                Text(translation,
                                    modifier=Modifier.fillMaxWidth().padding(horizontal=4.dp),
                                    textAlign=TextAlign.Center,
                                    color=if(active && settings.followMeal) RefRed
                                        else Color(0xFF42505A),
                                    fontSize=settings.mealSize.sp,
                                    lineHeight=(settings.mealSize*1.4f).sp,
                                    fontWeight=if(active && settings.followMeal)
                                        FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                        HorizontalDivider(color=if(active)Color(0xFFFFC1B9)
                            else Color(0xFFEDE9DE),thickness=.65.dp)
                    }
                }
            }
        }
        if(localPanel) LocalAudioPanel(surah.number,store,false)
        AyahAudioPanel(
            surah=surah, globalAyahOffset=globalAyahOffset,
            store=store,dark=true,requestedAyah=requestedVerse,
            playNonce=playNonce,onAyahChanged={playingVerse=it},
            onPlayingChanged={isPlaying=it},
            reciterId=settings.reciter,autoNext=settings.autoNext,
            onSettings={settingsOpen=true},
            onLocalAudio={localPanel=!localPanel}
        )
        Row(
            Modifier.fillMaxWidth().background(Color.White)
                .padding(vertical=2.dp),
            verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.SpaceEvenly
        ) {
            RefNavButton(Icons.Default.MenuBook,"Kur’an",true) {bookmarkedTarget=1}
            RefNavButton(Icons.Default.List,"Sureler",false,onBack)
            RefNavButton(Icons.Default.Search,"Ara",false) {searchOpen=!searchOpen}
            RefNavButton(Icons.Default.Bookmarks,"Yer İmleri",false) {
                bookmarkedTarget=store.bookmarks()
                    .firstOrNull{it.startsWith(surah.number.toString()+":")}
                    ?.substringAfter(":")?.toIntOrNull() ?:0
            }
            RefNavButton(Icons.Default.Settings,"Ayarlar",false) {settingsOpen=true}
        }
    }
    if(settingsOpen) {
        ReferenceAyahSettingsSheet(settings,onUpdate={saveSettings(it)},
            onDismiss={settingsOpen=false},
            onLocalAudio={settingsOpen=false;localPanel=true})
    }
}

@Composable
private fun RefNavButton(
    icon:androidx.compose.ui.graphics.vector.ImageVector,
    text:String,selected:Boolean,onClick:()->Unit
) {
    TextButton(onClick=onClick,
        contentPadding=PaddingValues(horizontal=2.dp,vertical=1.dp),
        colors=ButtonDefaults.textButtonColors(contentColor=RefGreen)) {
        Column(horizontalAlignment=Alignment.CenterHorizontally) {
            Icon(icon,text,modifier=Modifier.size(21.dp))
            Text(text,fontSize=10.sp,maxLines=1)
        }
    }
}

@Composable
private fun RefSurahBanner(surah:Surah) {
    Surface(color=Color(0xFFFFF8EA),border=BorderStroke(1.dp,RefGold),
        modifier=Modifier.fillMaxWidth().padding(horizontal=6.dp,vertical=3.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min=72.dp),
            verticalAlignment=Alignment.CenterVertically) {
            RefFloralMotif(Modifier.weight(.22f).height(72.dp))
            Column(Modifier.weight(.56f),
                horizontalAlignment=Alignment.CenterHorizontally) {
                Text(surah.arabicName,fontFamily=RefFont,color=RefDark,
                    fontSize=30.sp,lineHeight=40.sp,
                    textAlign=TextAlign.Center,style=TextStyle(textDirection=TextDirection.Rtl))
                Text(surah.turkishName.uppercase()+" SURESİ",fontSize=11.sp,
                    letterSpacing=1.sp,color=RefDark,textAlign=TextAlign.Center)
                if(surah.openingBasmala!=null) Text(surah.openingBasmala,
                    fontFamily=RefFont,fontSize=18.sp,lineHeight=28.sp,
                    color=RefDark,textAlign=TextAlign.Center,
                    style=TextStyle(textDirection=TextDirection.Rtl))
            }
            RefFloralMotif(Modifier.weight(.22f).height(72.dp))
        }
    }
}
@Composable
private fun RefFloralMotif(modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val green=Color(0xFF0A8C6B)
        val gold=Color(0xFFE6AD3F)
        val c=androidx.compose.ui.geometry.Offset(size.width/2,size.height/2)
        drawCircle(gold,radius=size.minDimension*.39f,center=c,style=Stroke(width=2f))
        for(i in 0 until 8) {
            val a=i*3.1415926f/4
            drawCircle(
                if(i%2==0)green else gold,
                radius=size.minDimension*.14f,
                center=androidx.compose.ui.geometry.Offset(
                    c.x+kotlin.math.cos(a)*size.minDimension*.23f,
                    c.y+kotlin.math.sin(a)*size.minDimension*.23f)
            )
        }
        drawCircle(Color(0xFFFFF8EA),radius=size.minDimension*.11f,center=c)
    }
}

@Composable
private fun RefColoredArabic(text:String,active:Boolean,settings:RefReadingSettings) {
    var chosen by remember(text){mutableIntStateOf(-1)}
    var layout by remember(text){mutableStateOf<TextLayoutResult?>(null)}
    val words=remember(text) {text.split(Regex("\\s+")).filter{it.isNotBlank()}}
    val annotated=buildAnnotatedString {
        words.forEachIndexed {index,word->
            if(index>0)append(" ")
            pushStringAnnotation("word",index.toString())
            withStyle(SpanStyle(color=when {
                active || chosen==index ->RefRed
                settings.colored ->RefPalette[index%RefPalette.size]
                else ->RefDark
            })) {append(word)}
            pop()
        }
    }
    val size=(settings.arabicSize+12).coerceIn(36,68)
    Text(annotated,modifier=Modifier.fillMaxWidth().padding(horizontal=2.dp)
        .pointerInput(text) {
            detectTapGestures {point->
                val index=layout?.getOffsetForPosition(point) ?:return@detectTapGestures
                chosen=annotated.getStringAnnotations("word",index,index)
                    .firstOrNull()?.item?.toIntOrNull() ?: -1
            }
        },
        fontSize=size.sp,lineHeight=(size*1.65f).sp,
        fontFamily=RefFont,textAlign=TextAlign.Center,
        style=TextStyle(textDirection=TextDirection.Rtl,fontSynthesis=FontSynthesis.None),
        onTextLayout={layout=it})
}
