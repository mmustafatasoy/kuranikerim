package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Emerald = Color(0xFF005046)
private val DarkEmerald = Color(0xFF003F37)
private val Gold = Color(0xFFD6A53C)
private val Red = Color(0xFFE52A2E)
private val WhitePaper = Color(0xFFFFFDFA)
private val SurahFont = FontFamily(Font(R.font.amiri_quran))
private val Colors = listOf(
    Color(0xFFDD4317), Color(0xFF116BDF), Color(0xFF159137),
    Color(0xFF9F21BA), Color(0xFFE01983), Color(0xFF007FAC),
    Color(0xFFD18A00)
)

/**
 * Readable, traditional Qur'anic Naskh letterforms via Amiri Quran (OFL 1.1).
 * The source Arabic ayah content is NEVER reconstructed from an image.
 * Audio emphasis is verse-accurate, not fabricated character timestamps.
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
    var playedAyah by remember(surah.number) { mutableIntStateOf(0) }
    var soundPlaying by remember(surah.number) { mutableStateOf(false) }
    var requestedAyah by remember(surah.number) { mutableIntStateOf(saved) }
    var playRequest by remember(surah.number) { mutableIntStateOf(0) }
    var bookmarkRevision by remember(surah.number) { mutableIntStateOf(0) }
    var mealVisible by remember(surah.number) { mutableStateOf(true) }
    var showMp3 by remember(surah.number) { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    var meal by remember(surah.number) { mutableStateOf<List<String>?>(null) }
    var mealError by remember(surah.number) { mutableStateOf(false) }
    var retry by remember(surah.number) { mutableIntStateOf(0) }
    var bookmarkTarget by remember(surah.number) { mutableIntStateOf(0) }

    LaunchedEffect(surah.number, retry) {
        mealError = false
        try {
            meal = TurkishMealRepository(context.applicationContext)
                .surah(surah.number, surah.ayahCount)
        } catch (_: Exception) {
            mealError = true
        }
    }
    // Save the actual visible verse. Scrolling the Quran does not lose progress.
    LaunchedEffect(surah.number, scroll) {
        snapshotFlow { scroll.firstVisibleItemIndex }.distinctUntilChanged().collect { position ->
            store.savePosition(surah.number, (position + 1).coerceIn(1, surah.ayahCount))
        }
    }
    LaunchedEffect(playedAyah, soundPlaying) {
        if (soundPlaying && playedAyah in 1..surah.ayahCount) {
            scroll.animateScrollToItem(playedAyah - 1)
        }
    }
    LaunchedEffect(bookmarkTarget) {
        if (bookmarkTarget in 1..surah.ayahCount) scroll.animateScrollToItem(bookmarkTarget - 1)
    }

    Column(Modifier.fillMaxSize().background(WhitePaper)) {
        // The white page and emerald header follow the reference screenshot.
        Row(
            modifier = Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(DarkEmerald, Color(0xFF087662)))
            ).padding(horizontal = 5.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
            }
            Column(
                Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(surah.turkishName.uppercase() + " SURESİ",
                    color = Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, maxLines = 1)
                Text(surah.number.toString() + ". Sure • " + surah.ayahCount + " Ayet",
                    color = Color(0xFFDEF3E8), fontSize = 12.sp)
            }
            IconButton(onClick = {
                val target = (scroll.firstVisibleItemIndex + 1).coerceIn(1, surah.ayahCount)
                store.toggleBookmark(surah.number, target)
                bookmarkRevision++
            }) {
                Icon(Icons.Default.BookmarkBorder, "Görünen ayeti kaydet", tint = Gold)
            }
            IconButton(onClick = { settingsOpen = true }) {
                Icon(Icons.Default.Settings, "Ayarlar", tint = Color.White)
            }
        }

        QuranSurahBanner(surah)

        if (mealVisible && meal == null) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mealError) {
                    Text("Türkçe meal alınamadı. Bağlantını kontrol et.",
                        color = Red, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { retry++ }) { Text("Yenile") }
                } else {
                    CircularProgressIndicator(Modifier.size(15.dp),
                        strokeWidth = 2.dp, color = Emerald)
                    Spacer(Modifier.width(8.dp))
                    Text("Türkçe meal yükleniyor", color = Emerald, fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            state = scroll, modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 9.dp, end = 9.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(surah.verses, key = { index, _ -> index }) { index, arabic ->
                val number = index + 1
                val active = soundPlaying && playedAyah == number
                val bookmark = remember(bookmarkRevision, number) {
                    store.hasBookmark(surah.number, number)
                }
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    color = if (active) Color(0xFFFFE8E5) else Color.White,
                    border = BorderStroke(if (active) 1.5.dp else 0.5.dp,
                        if (active) Color(0xFFFF9791) else Color(0xFFF1E7D7))
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            AyahMedallion(number, active)
                            Spacer(Modifier.weight(1f))
                            if (active) Text("● OKUNUYOR", color = Red, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold)
                            IconButton(
                                modifier = Modifier.size(40.dp),
                                onClick = { requestedAyah = number; playRequest++ }
                            ) {
                                Icon(Icons.Default.PlayCircle, "Bu ayeti sesli oku",
                                    tint = if (active) Red else Emerald, modifier = Modifier.size(30.dp))
                            }
                            IconButton(
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                    store.toggleBookmark(surah.number, number)
                                    bookmarkRevision++
                                }
                            ) {
                                Icon(if (bookmark) Icons.Default.Bookmark
                                     else Icons.Default.BookmarkBorder,
                                    "Yer imi", tint = if (bookmark) Gold else Emerald)
                            }
                        }

                        ColoredQuranVerse(
                            arabic = arabic,
                            playingVerse = active,
                            size = (fontSize + 13).coerceIn(38, 65)
                        )

                        if (mealVisible) {
                            meal?.getOrNull(index)?.let { sentence ->
                                Text(
                                    text = sentence,
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (active) Red else Color(0xFF435457),
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                        HorizontalDivider(
                            color = if (active) Color(0xFFFFCBC4) else Color(0xFFF3E8D1),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        if (showMp3) LocalAudioPanel(surah.number, store, false)

        AyahAudioPanel(
            surah = surah,
            globalAyahOffset = globalAyahOffset,
            store = store,
            dark = true,
            requestedAyah = requestedAyah,
            playNonce = playRequest,
            onAyahChanged = { playedAyah = it },
            onPlayingChanged = { soundPlaying = it }
        )

        // Working bottom controls rather than image-only icons.
        Row(
            Modifier.fillMaxWidth().background(Color.White)
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ReaderTab(Icons.Default.MenuBook, "Kur’an", onClick = {
                bookmarkTarget = 1
            }, selected = true)
            ReaderTab(Icons.Default.List, "Sureler", onClick = onBack)
            ReaderTab(Icons.Default.Search, "Ara", onClick = onBack)
            ReaderTab(Icons.Default.Bookmarks, "Yer İmleri", onClick = {
                bookmarkTarget = store.bookmarks().firstOrNull {
                    it.startsWith(surah.number.toString() + ":")
                }?.substringAfter(":")?.toIntOrNull() ?: 0
            })
            ReaderTab(Icons.Default.Settings, "Ayarlar", onClick = { settingsOpen = true })
        }
    }

    if (settingsOpen) {
        AlertDialog(
            onDismissRequest = { settingsOpen = false },
            title = { Text("Kur’an okuma ayarları") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Büyük geleneksel Amiri Quran yazısı")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onFontSize((fontSize - 2).coerceAtLeast(24)) }) {
                            Icon(Icons.Default.Remove, "Küçült")
                        }
                        Text("Arapça boyutu: " + (fontSize + 13).coerceIn(38, 65),
                            modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        IconButton(onClick = { onFontSize((fontSize + 2).coerceAtMost(52)) }) {
                            Icon(Icons.Default.Add, "Büyüt")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Türkçe meal göster", modifier = Modifier.weight(1f))
                        Switch(checked = mealVisible, onCheckedChange = { mealVisible = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Yerel MP3 paneli", modifier = Modifier.weight(1f))
                        Switch(checked = showMp3, onCheckedChange = { showMp3 = it })
                    }
                    Text("Kırmızı vurgu kıraat sırasında aktif ayeti belirtir. " +
                         "Harf zamanlaması henüz bulunmuyor.", fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { settingsOpen = false }) { Text("Tamam") }
            }
        )
    }
}

@Composable
private fun QuranSurahBanner(surah: Surah) {
    Column(
        Modifier.fillMaxWidth()
            .background(Color(0xFFFFF8EA))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("۞", color = Gold, fontSize = 29.sp)
            Column(
                Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(surah.arabicName, color = Emerald, fontFamily = SurahFont,
                    fontSize = 29.sp, lineHeight = 43.sp,
                    style = TextStyle(textDirection = TextDirection.Rtl))
                Text(surah.turkishName.uppercase() + " SURESİ",
                    color = Emerald, fontSize = 11.sp, letterSpacing = 1.sp)
            }
            Text("۞", color = Gold, fontSize = 29.sp)
        }
        // Fatiha: its numbered ayah 1 is already Basmala (do not duplicate).
        // Tawbah: no Basmala. Other surahs: introductory, UNNUMBERED Basmala.
        if (surah.openingBasmala != null) {
            Text(surah.openingBasmala, color = Emerald,
                fontFamily = SurahFont, fontSize = 26.sp, lineHeight = 41.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(textDirection = TextDirection.Rtl),
                modifier = Modifier.fillMaxWidth())
        }
        HorizontalDivider(color = Gold, thickness = 1.dp)
    }
}

@Composable
private fun ReaderTab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 3.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) Color(0xFF007A5F) else Emerald
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, label, modifier = Modifier.size(21.dp))
            Text(label, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
private fun AyahMedallion(number: Int, isPlaying: Boolean) {
    Box(Modifier.size(39.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outer = size.minDimension * 0.49f
            val inner = outer * 0.79f
            val star = Path()
            for (i in 0..16) {
                val angle = (i * PI / 8 - PI / 2).toFloat()
                val radius = if (i % 2 == 0) outer else inner
                val x = cx + radius * cos(angle)
                val y = cy + radius * sin(angle)
                if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
            }
            star.close()
            drawPath(star, color = if (isPlaying) Color(0xFFFFD8D3)
                      else Color(0xFFFFF5D7))
            drawPath(star, color = if (isPlaying) Red else Gold,
                style = Stroke(width = 2.5f))
        }
        Text(number.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold,
            color = if (isPlaying) Red else Emerald)
    }
}

@Composable
private fun ColoredQuranVerse(
    arabic: String,
    playingVerse: Boolean,
    size: Int
) {
    var selectedWord by remember(arabic) { mutableIntStateOf(-1) }
    var textLayout by remember(arabic) { mutableStateOf<TextLayoutResult?>(null) }
    // Color complete Arabic words, not individual code points: do not break
    // joining, diacritics, tashkeel, tajweed symbols or glyph shaping.
    val words = remember(arabic) {
        arabic.split(Regex("\\s+")).filter { it.isNotEmpty() }
    }
    val colored = buildAnnotatedString {
        words.forEachIndexed { index, word ->
            if (index != 0) append(" ")
            pushStringAnnotation(tag = "word", annotation = index.toString())
            withStyle(
                SpanStyle(color = if (playingVerse || selectedWord == index) Red
                                 else Colors[index % Colors.size])
            ) { append(word) }
            pop()
        }
    }
    Text(
        text = colored,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
            .pointerInput(arabic) {
                detectTapGestures { point ->
                    val index = textLayout?.getOffsetForPosition(point)
                        ?: return@detectTapGestures
                    selectedWord = colored.getStringAnnotations(
                        tag = "word", start = index, end = index
                    ).firstOrNull()?.item?.toIntOrNull() ?: -1
                }
            },
        fontFamily = SurahFont,
        fontSize = size.sp,
        lineHeight = (size * 1.7f).sp,
        textAlign = TextAlign.Center,
        style = TextStyle(textDirection = TextDirection.Rtl, fontSynthesis = FontSynthesis.None),
        onTextLayout = { textLayout = it }
    )
}
