package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

private val Green = Color(0xFF005447)
private val Gold = Color(0xFFD8A642)
private val Red = Color(0xFFE82B35)
private val WhitePaper = Color(0xFFFFFCF7)
private val WordColors = listOf(
    Color(0xFFE34D1A), Color(0xFF056CEB), Color(0xFF059C46),
    Color(0xFF9C19CF), Color(0xFFDF1884), Color(0xFF008FAD),
    Color(0xFFD58D00)
)

/**
 * White Quran page inspired by user's reference. Red means currently playing
 * AYAH, not a fictional millisecond-accurate word/letter timestamp.
 */
@Composable
fun PremiumReaderScreen(
    surah: Surah,
    globalAyahOffset: Int,
    store: ProgressStore,
    fontSize: Int,
    onFontSize: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val saved = remember(surah.number) {
        store.position().takeIf { it.surah == surah.number }?.ayah
            ?.coerceIn(1, surah.ayahCount) ?: 1
    }
    val scroll = rememberLazyListState(initialFirstVisibleItemIndex = saved - 1)
    var playingAyah by remember(surah.number) { mutableIntStateOf(0) }
    var soundPlaying by remember(surah.number) { mutableStateOf(false) }
    var requested by remember(surah.number) { mutableIntStateOf(saved) }
    var playRequest by remember(surah.number) { mutableIntStateOf(0) }
    var bookmarkRevision by remember(surah.number) { mutableIntStateOf(0) }
    var showMeal by remember(surah.number) { mutableStateOf(true) }
    var showMp3 by remember(surah.number) { mutableStateOf(false) }
    var meal by remember(surah.number) { mutableStateOf<List<String>?>(null) }
    var mealError by remember(surah.number) { mutableStateOf(false) }
    var retry by remember(surah.number) { mutableIntStateOf(0) }
    var jumpTo by remember(surah.number) { mutableIntStateOf(0) }

    LaunchedEffect(surah.number, retry) {
        mealError = false
        try {
            meal = TurkishMealRepository(context.applicationContext)
                .surah(surah.number, surah.ayahCount)
        } catch (_: Exception) { mealError = true }
    }
    LaunchedEffect(surah.number, scroll) {
        snapshotFlow { scroll.firstVisibleItemIndex }.distinctUntilChanged().collect { index ->
            store.savePosition(surah.number, (index + 1).coerceIn(1, surah.ayahCount))
        }
    }
    LaunchedEffect(playingAyah, soundPlaying) {
        if (soundPlaying && playingAyah in 1..surah.ayahCount) {
            scroll.animateScrollToItem(playingAyah - 1)
        }
    }
    LaunchedEffect(jumpTo) {
        if (jumpTo in 1..surah.ayahCount) scroll.animateScrollToItem(jumpTo - 1)
    }

    Column(Modifier.fillMaxSize().background(WhitePaper)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(Color(0xFF003D36), Color(0xFF09836B)))
            ).padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(surah.turkishName.uppercase() + " SURESİ",
                    color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(surah.number.toString() + ". Sure • " + surah.ayahCount + " Ayet",
                    color = Color(0xFFDDF1E6), fontSize = 12.sp)
            }
            IconButton(onClick = { showMeal = !showMeal }) {
                Icon(Icons.Default.Translate, "Meali göster veya gizle", tint = Color.White)
            }
        }
        Column(
            Modifier.fillMaxWidth().background(Color(0xFFFFF6E6))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✦ ۞ ✦", color = Gold, fontSize = 23.sp)
            Text(surah.arabicName, color = Green, fontSize = 35.sp,
                style = TextStyle(textDirection = TextDirection.Rtl))
            Text(surah.turkishName + " Suresi", color = Green, fontSize = 13.sp)
            HorizontalDivider(color = Gold, thickness = 1.dp)
        }
        if (showMeal && meal == null) {
            Row(Modifier.fillMaxWidth().padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
                if (mealError) {
                    Text("Meal yüklenemedi; internet bağlantısını kontrol et.",
                        color = Red, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { retry++ }) { Text("Yenile") }
                } else {
                    CircularProgressIndicator(Modifier.size(15.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.size(7.dp))
                    Text("Türkçe meal yükleniyor…", color = Green, fontSize = 12.sp)
                }
            }
        }
        LazyColumn(
            state = scroll, modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(surah.verses, key = { index, _ -> index }) { index, arabic ->
                val ayah = index + 1
                val active = soundPlaying && playingAyah == ayah
                val hasBookmark = remember(bookmarkRevision, ayah) {
                    store.hasBookmark(surah.number, ayah)
                }
                Surface(
                    color = if (active) Color(0xFFFFE9E8) else Color.White,
                    shape = RoundedCornerShape(17.dp),
                    shadowElevation = 2.dp,
                    border = BorderStroke(if (active) 1.5.dp else 0.6.dp,
                        if (active) Red else Color(0xFFE7D6A7))
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 7.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (active) Color(0xFFFFD3D1) else Color(0xFFFFF0C7),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (active) Red else Gold)
                            ) {
                                Text(ayah.toString(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = if (active) Red else Green,
                                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.weight(1f))
                            if (active) Text("● OKUNUYOR", color = Red, fontSize = 11.sp)
                            IconButton(onClick = { requested = ayah; playRequest++ }) {
                                Icon(Icons.Default.PlayCircle, "Ayeti sesli oku",
                                    tint = if (active) Red else Green)
                            }
                            IconButton(onClick = {
                                store.toggleBookmark(surah.number, ayah); bookmarkRevision++
                            }) {
                                Icon(if (hasBookmark) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    "Yer imi", tint = if (hasBookmark) Gold else Green)
                            }
                        }
                        RainbowText(arabic, active, (fontSize + 10).coerceIn(36, 62))
                        if (showMeal) {
                            val translation = meal?.getOrNull(index)
                            if (translation != null) {
                                Text(translation,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                    color = if (active) Red else Color(0xFF435257))
                            } else if (mealError) {
                                Text("Meal henüz kullanılabilir değil.", fontSize = 11.sp,
                                    color = Color.Gray)
                            }
                        }
                        Text("✧", textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(), color = Gold)
                    }
                }
            }
        }
        AyahAudioPanel(
            surah = surah, globalAyahOffset = globalAyahOffset,
            store = store, dark = true, requestedAyah = requested,
            playNonce = playRequest,
            onAyahChanged = { playingAyah = it },
            onPlayingChanged = { soundPlaying = it }
        )
        Row(
            Modifier.fillMaxWidth().background(Color.White)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.Default.MenuBook, "Sureler", tint = Green)
                Text(" Sureler", color = Green)
            }
            IconButton(onClick = { onFontSize((fontSize - 2).coerceAtLeast(24)) }) {
                Icon(Icons.Default.Remove, "Harfleri küçült", tint = Green)
            }
            Text("Aa", color = Green, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onFontSize((fontSize + 2).coerceAtMost(52)) }) {
                Icon(Icons.Default.Add, "Harfleri büyüt", tint = Green)
            }
            IconButton(onClick = {
                jumpTo = store.bookmarks()
                    .firstOrNull { it.startsWith(surah.number.toString() + ":") }
                    ?.substringAfter(":")?.toIntOrNull() ?: 0
            }) {
                Icon(Icons.Default.Bookmarks, "Yer imine git", tint = Gold)
            }
        }
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { showMp3 = !showMp3 }
        ) {
            Text(if (showMp3) "MP3 panelini gizle" else "Çevrimdışı yerel MP3 aç",
                color = Green, fontSize = 11.sp)
        }
        if (showMp3) LocalAudioPanel(surah.number, store, false)
    }
}

@Composable
private fun RainbowText(arabic: String, playingVerse: Boolean, size: Int) {
    var selectedWord by remember(arabic) { mutableIntStateOf(-1) }
    var layout by remember(arabic) { mutableStateOf<TextLayoutResult?>(null) }
    val words = remember(arabic) {
        arabic.split(Regex("""\s+""")).filter { it.isNotEmpty() }
    }
    val annotated = buildAnnotatedString {
        words.forEachIndexed { index, word ->
            if (index != 0) append(" ")
            pushStringAnnotation(tag = "word", annotation = index.toString())
            withStyle(SpanStyle(
                color = if (playingVerse || index == selectedWord) Red
                else WordColors[index % WordColors.size],
                fontWeight = FontWeight.SemiBold
            )) { append(word) }
            pop()
        }
    }
    Text(
        text = annotated,
        modifier = Modifier.fillMaxWidth().pointerInput(arabic) {
            detectTapGestures { position ->
                val offset = layout?.getOffsetForPosition(position) ?: return@detectTapGestures
                selectedWord = annotated.getStringAnnotations("word", offset, offset)
                    .firstOrNull()?.item?.toIntOrNull() ?: -1
            }
        },
        style = TextStyle(textDirection = TextDirection.Rtl),
        fontSize = size.sp,
        lineHeight = (size * 1.75f).sp,
        textAlign = TextAlign.Center,
        onTextLayout = { layout = it }
    )
}
