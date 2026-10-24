package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

internal val RefGreen = Color(0xFF005C4F)
internal val RefDark = Color(0xFF00473C)
internal val RefGold = Color(0xFFD2A347)
internal val RefPaper = Color(0xFFFFFDF8)
internal val RefRed = Color(0xFFE83B32)
internal val RefFont = FontFamily(Font(R.font.amiri_quran))
internal val RefPalette = listOf(Color(0xFF156BD7),Color(0xFF148D41),Color(0xFFEF8A10),
    Color(0xFF9A24C9),Color(0xFFE2227D),Color(0xFF007FA0))

@Composable
internal fun RefTopBar(title:String, subtitle:String, onBack:(()->Unit)?=null,
    onBookmark:(()->Unit)?=null, onSettings:(()->Unit)?=null) {
    Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(RefDark,RefGreen)))
        .heightIn(min=65.dp).padding(horizontal=4.dp), verticalAlignment=Alignment.CenterVertically) {
        if(onBack!=null) IconButton(onClick=onBack) {
            Icon(Icons.Default.ArrowBack,"Geri",tint=Color.White)
        } else Spacer(Modifier.width(48.dp))
        Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
            Text(title, fontSize=19.sp,fontWeight=FontWeight.Bold,color=Color.White,maxLines=1)
            Text(subtitle,fontSize=11.sp,color=Color(0xFFD2EEE0),maxLines=1)
        }
        if(onBookmark!=null) IconButton(onClick=onBookmark) {
            Icon(Icons.Default.BookmarkBorder,"Yer imi",tint=RefGold)
        }
        if(onSettings!=null) IconButton(onClick=onSettings) {
            Icon(Icons.Default.Settings,"Ayet ayarları",tint=RefGold)
        }
        if(onBookmark==null && onSettings==null) Spacer(Modifier.width(48.dp))
    }
}

@Composable
internal fun RefBadge(number:Int, active:Boolean=false, modifier:Modifier=Modifier) {
    Box(modifier.size(43.dp),contentAlignment=Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val p=Path();val x=size.width/2;val y=size.height/2
            for(i in 0..16) {
                val a=i*PI/8-PI/2; val r=size.minDimension*(if(i%2==0) .48f else .39f)
                val px=x+cos(a).toFloat()*r;val py=y+sin(a).toFloat()*r
                if(i==0)p.moveTo(px,py) else p.lineTo(px,py)
            }
            p.close()
            drawPath(p,color=if(active) Color(0xFFFFE5E0) else Color(0xFFFFF5D6))
            drawPath(p,color=if(active) RefRed else RefGold,style=Stroke(width=2.dp.toPx()))
        }
        Text(number.toString(),fontSize=16.sp,fontWeight=FontWeight.Bold,
            color=if(active)RefRed else RefDark)
    }
}

@Composable
internal fun ReferenceSurahListScreen(repo:QuranRepository,onOpen:(Int)->Unit,
    focusSearch:Boolean=false) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Tümü") }
    val focus=remember { FocusRequester() }
    LaunchedEffect(focusSearch) { if(focusSearch) focus.requestFocus() }
    val shown=remember(query,filter,repo) {
        repo.surahs.filter {
            (filter=="Tümü"||(filter=="Mekke"&&it.revelation.equals("Meccan",true))||
                (filter=="Medine"&&it.revelation.equals("Medinan",true))) &&
            (query.isBlank()||it.turkishName.contains(query,true)||it.latinName.contains(query,true)||
                it.arabicName.contains(query)||it.number.toString()==query.trim())
        }
    }
    Column(Modifier.fillMaxSize().background(RefPaper)) {
        RefTopBar("Sureler","Toplam 114 Sure")
        OutlinedTextField(value=query,onValueChange={query=it}, singleLine=true,
            modifier=Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=8.dp).focusRequester(focus),
            placeholder={Text("Sure ara…")}, leadingIcon={Icon(Icons.Default.Search,null)},
            trailingIcon={if(query.isNotBlank())IconButton(onClick={query=""}) {
                Icon(Icons.Default.Close,"Temizle")
            }},
            shape=RoundedCornerShape(23.dp),colors=OutlinedTextFieldDefaults.colors(
                focusedTextColor=Color.Black,unfocusedTextColor=Color.Black,
                focusedBorderColor=RefGreen,unfocusedBorderColor=Color(0xFFE2E3DD)))
        Row(Modifier.fillMaxWidth().padding(horizontal=12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Tümü","Mekke","Medine").forEach { category ->
                FilterChip(selected=filter==category,onClick={filter=category},label={Text(category)},
                    colors=FilterChipDefaults.filterChipColors(
                        selectedContainerColor=RefGreen,selectedLabelColor=Color.White))
            }
        }
        LazyColumn(Modifier.weight(1f),contentPadding=PaddingValues(10.dp),
            verticalArrangement=Arrangement.spacedBy(5.dp)) {
            items(shown,key={it.number}) { s ->
                Surface(onClick={onOpen(s.number)},shape=RoundedCornerShape(16.dp),
                    border=BorderStroke(.6.dp,Color(0xFFE7E5DE)),color=Color.White) {
                    Row(Modifier.fillMaxWidth().padding(10.dp),verticalAlignment=Alignment.CenterVertically) {
                        RefBadge(s.number)
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(s.turkishName,fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color(0xFF202C28))
                            Text(s.ayahCount.toString()+" Ayet",fontSize=12.sp,color=Color.Gray)
                        }
                        Column(horizontalAlignment=Alignment.End) {
                            Text(s.arabicName,fontFamily=RefFont,fontSize=24.sp,
                                color=RefDark,style=TextStyle(textDirection=TextDirection.Rtl))
                            Text(if(s.revelation.equals("Medinan",true))"Medine" else "Mekke",
                                fontSize=11.sp,color=RefGreen)
                        }
                        Icon(Icons.Default.ChevronRight,null,tint=RefGreen)
                    }
                }
            }
        }
    }
}

@Composable
internal fun ReferenceAppSettings(
    dark:Boolean,
    onTheme:()->Unit,
    onCheckUpdates:()->Unit = {}
) {
    Column(Modifier.fillMaxSize().background(RefPaper)) {
        RefTopBar("Ayarlar","Uygulama tercihleri")
        Row(Modifier.fillMaxWidth().padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
            Text("Koyu ana ekran teması",modifier=Modifier.weight(1f),color=RefDark)
            Switch(checked=dark,onCheckedChange={onTheme()})
        }
        HorizontalDivider()
        Surface(
            onClick = onCheckUpdates,
            color = Color.White,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SystemUpdate, "Güncellemeleri kontrol et", tint = RefGreen)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Güncellemeleri kontrol et",
                        fontWeight = FontWeight.Bold, color = RefDark)
                    Text("GitHub Releases • NUR yeni sürümleri",
                        fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.ChevronRight, null, tint = RefGreen)
            }
        }
        HorizontalDivider()
        Text("Arapça yazı, meal ve ses ayarları sure okunurken sağ üstteki ayar düğmesinde.",
            modifier=Modifier.padding(18.dp),color=RefGreen)
    }
}
