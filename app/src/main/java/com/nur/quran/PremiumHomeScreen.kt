package com.nur.quran

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

private val night = Color(0xFF042C2A)
private val emerald = Color(0xFF006657)
private val aqua = Color(0xFF00A88F)
private val gold = Color(0xFFFFD787)
private val ink = Color(0xFF103E36)
private val ivory = Color(0xFFFFF9EE)

@Composable
internal fun PremiumHomeScreen(
    store: ProgressStore,
    repo: QuranRepository,
    dark: Boolean,
    onContinue: () -> Unit,
    onSurahList: () -> Unit,
    onLearn: () -> Unit,
    onWorship: () -> Unit,
    onLibrary: () -> Unit,
    onSearch: () -> Unit,
    onUpdates: () -> Unit,
    onTheme: () -> Unit,
    onShortSurah: (Int) -> Unit
) {
    val position = store.position()
    val surah = repo.surah(position.surah)
    val paper = if (dark) Color(0xFF0A2D29) else ivory
    val text = if (dark) Color(0xFFF8EFD9) else ink
    val muted = if (dark) Color(0xFFC0D6C8) else Color(0xFF667B73)
    Column(
        modifier = Modifier.fillMaxSize().background(paper).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF032726), Color(0xFF075C50), Color(0xFF03473E))
                    )
                ).padding(start = 16.dp, end = 16.dp, top = 15.dp, bottom = 27.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(13.dp),
                        color = Color(0xFFE9C27A),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoStories, null, tint = night,
                                modifier = Modifier.size(26.dp))
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("NUR", fontWeight = FontWeight.Black, color = Color.White,
                            fontSize = 26.sp, letterSpacing = 3.sp, lineHeight = 27.sp)
                        Text("KUR'AN & YAŞAM", color = Color(0xFFDDEFE4),
                            fontSize = 9.sp, letterSpacing = 1.2.sp)
                    }
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Default.Search, "Sure ara", tint = gold,
                            modifier = Modifier.size(27.dp))
                    }
                    IconButton(onClick = onUpdates) {
                        Icon(Icons.Default.Notifications, "Güncelleme bildirimi",
                            tint = gold, modifier = Modifier.size(25.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                HomeMosqueSkyline(Modifier.fillMaxWidth().height(66.dp))
                Spacer(Modifier.height(7.dp))
                Text("✦  BİR ÖMÜRLÜK YOLCULUK  ✦",
                    color = gold, fontSize = 11.sp, letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedCalligraphySeal(
                        inscription = "ٱللّٰهُ",
                        sizeDp = 120,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Kalbine\ndokunan\nbir yolculuk",
                            color = Color(0xFFFFF4DC), fontSize = 19.sp,
                            lineHeight = 25.sp, textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold, maxLines = 3)
                    }
                    AnimatedCalligraphySeal(
                        inscription = "مُحَمَّدٌ",
                        sizeDp = 120,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Text("Kur'an-ı Kerim, ibadet ve öğrenme\ntek bir uygulamada.",
                    color = Color(0xFFDAEDE5), fontSize = 12.sp,
                    textAlign = TextAlign.Center, lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFFFF4D9),
                    shape = RoundedCornerShape(21.dp),
                    border = BorderStroke(2.dp, Color(0xFFE3B960)),
                    shadowElevation = 5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 9.dp, vertical = 13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ",
                            fontFamily = RefFont, fontSize = 25.sp, lineHeight = 45.sp,
                            color = ink, textAlign = TextAlign.Center,
                            style = TextStyle(textDirection = TextDirection.Rtl))
                        Text("✦  ❖  ✦", color = Color(0xFFC09347), fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            color = if (dark) Color(0xFF19493E) else Color.White,
            shape = RoundedCornerShape(23.dp),
            border = BorderStroke(1.dp, Color(0xFFD8BA80)),
            shadowElevation = 4.dp
        ) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF0A775F), shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.size(57.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MenuBook, null, tint = gold,
                            modifier = Modifier.size(35.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("KALDIĞIN YERDEN DEVAM ET", color = if (dark) gold
                        else Color(0xFF947136), fontSize = 9.sp, letterSpacing = .7.sp,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text(surah.turkishName + " Suresi", fontSize = 19.sp,
                        fontWeight = FontWeight.Bold, color = text, maxLines = 1)
                    Text(position.ayah.toString() + ". ayet • Okumaya devam et",
                        color = muted, fontSize = 12.sp)
                }
                Icon(Icons.Default.ArrowCircleRight, "Devam et", tint = emerald,
                    modifier = Modifier.size(35.dp))
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(start = 17.dp, end = 15.dp, top = 22.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Keşfet & Öğren", color = text, fontSize = 25.sp,
                    fontWeight = FontWeight.Bold)
                Text("Her şey tek bir uygulamada", color = muted, fontSize = 12.sp)
            }
            TextButton(onClick = onWorship) {
                Text("Tüm modüller ›", color = if (dark) gold else emerald, fontSize = 12.sp)
            }
        }
        Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                VividHomeCard(
                    title = "Kur'an-ı Kerim", subtitle = "114 sure, Arapça & Türkçe meal",
                    icon = Icons.Default.AutoStories,
                    palette = listOf(Color(0xFF003A35), Color(0xFF028878), Color(0xFF12BAAA)),
                    foreground = Color.White, modifier = Modifier.weight(1f),
                    onClick = onSurahList
                )
                VividHomeCard(
                    title = "İbadet Rehberi", subtitle = "Namaz, abdest, dua ve zikir",
                    icon = Icons.Default.Mosque,
                    palette = listOf(Color(0xFFFFF0C6), Color(0xFFF9D07C), Color(0xFFFFDDA9)),
                    foreground = ink, modifier = Modifier.weight(1f),
                    onClick = onWorship
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                VividHomeCard(
                    title = "Elifba Öğren", subtitle = "Arapça harfleri kolayca öğren",
                    icon = Icons.Default.School,
                    palette = listOf(Color(0xFFA6F5EA), Color(0xFF57DCCB), Color(0xFF00B7B8)),
                    foreground = ink, modifier = Modifier.weight(1f), onClick = onLearn
                )
                VividHomeCard(
                    title = "Sesli Kütüphane", subtitle = "Kıraat, meal ve çevrimdışı dinleme",
                    icon = Icons.Default.Headphones,
                    palette = listOf(Color(0xFFFFD8C8), Color(0xFFFFA89F), Color(0xFFEE827B)),
                    foreground = ink, modifier = Modifier.weight(1f), onClick = onLibrary
                )
            }
        }
        Spacer(Modifier.height(17.dp))
        Surface(
            onClick = { onShortSurah(2) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp),
            color = Color(0xFF174D42),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFE9C474))
        ) {
            Box(Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF073D38), Color(0xFF177566), Color(0xFFAD7B35))
                )
            )) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoStories, null, tint = gold,
                            modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("GÜNÜN AYETİ", color = gold, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("إِنَّ اللّٰهَ مَعَ الصَّابِرِينَ",
                        color = Color(0xFFFFF2C8), fontFamily = RefFont,
                        fontSize = 26.sp, lineHeight = 44.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(textDirection = TextDirection.Rtl))
                    Text("“Şüphesiz Allah sabredenlerle beraberdir.”",
                        fontSize = 15.sp, color = Color.White, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(), lineHeight = 23.sp)
                    Text("Bakara Suresi • 153. Ayet   ›", color = gold, fontSize = 11.sp,
                        textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Hızlı dinle • Çevrimdışı hazır", color = text,
            fontWeight = FontWeight.Bold, fontSize = 17.sp,
            modifier = Modifier.padding(horizontal = 17.dp, vertical = 6.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Fâtiha" to 1, "İhlâs" to 112, "Felak" to 113, "Nâs" to 114)
                .forEach { (name, number) ->
                    OutlinedButton(
                        onClick = { onShortSurah(number) },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFD7B36B)),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text(name, color = if (dark) gold else emerald,
                            fontSize = 11.sp, maxLines = 1)
                    }
                }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onTheme) {
                Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    "Temayı değiştir", tint = if (dark) gold else emerald)
                Text(if (dark) "Açık görünüm" else "Koyu görünüm",
                    color = if (dark) gold else emerald, fontSize = 12.sp)
            }
        }
        Text("Kur'an metni: Tanzil • Meal: Şaban Piriş / QuranEnc",
            color = muted, fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
    }
}

@Composable
private fun VividHomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    palette: List<Color>,
    foreground: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(21.dp),
        border = BorderStroke(1.dp, if (foreground == Color.White)
            Color(0xFF57DBBF) else Color(0xFFE6C281)),
        shadowElevation = 3.dp,
        color = palette.first()
    ) {
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(palette)
        )) {
            Canvas(Modifier.fillMaxSize()) {
                val r = size.minDimension * .38f
                drawCircle(foreground.copy(alpha = .09f), r,
                    Offset(size.width * .98f, size.height * .18f),
                    style = Stroke(width = 8.dp.toPx()))
                drawCircle(foreground.copy(alpha = .09f), r * .77f,
                    Offset(size.width * .98f, size.height * .18f),
                    style = Stroke(width = 3.dp.toPx()))
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = if (foreground == Color.White) Color(0x4426F5D1)
                    else Color(0xAAFFF2D9),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = foreground,
                        modifier = Modifier.padding(10.dp).size(27.dp))
                }
                Column {
                    Text(title, color = foreground, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, lineHeight = 21.sp, maxLines = 2)
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, color = foreground.copy(alpha = .83f),
                        fontSize = 11.sp, lineHeight = 15.sp, maxLines = 2)
                    Spacer(Modifier.height(3.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Icon(Icons.Default.ArrowCircleRight, null, tint = foreground,
                            modifier = Modifier.size(25.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMosqueSkyline(modifier: Modifier = Modifier) {
    val alpha = Color(0xFF99D0B9)
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val buildings = listOf(.14f, .27f, .50f, .72f, .85f)
        for ((i, x) in buildings.withIndex()) {
            val center = w * x
            val tower = if (i == 2) h * .58f else h * .35f
            val rw = if (i == 2) 29.dp.toPx() else 9.dp.toPx()
            val top = h - tower
            drawRect(alpha.copy(alpha = .32f),
                topLeft = Offset(center - rw / 2f, top),
                size = androidx.compose.ui.geometry.Size(rw, tower))
            drawCircle(alpha.copy(alpha = .38f), radius = rw * .53f,
                center = Offset(center, top))
            drawLine(gold.copy(alpha = .46f),
                Offset(center, top - rw * .8f),
                Offset(center, top), 1.5.dp.toPx())
        }
        drawLine(alpha.copy(alpha = .42f), Offset(0f, h - 1f), Offset(w, h - 1f),
            1.dp.toPx())
        val moon = Offset(w * .58f, h * .22f)
        drawCircle(gold.copy(alpha = .82f), 13.dp.toPx(), moon)
        drawCircle(Color(0xFF05463F), 12.dp.toPx(),
            Offset(moon.x + 5.dp.toPx(), moon.y - 4.dp.toPx()))
    }
}
