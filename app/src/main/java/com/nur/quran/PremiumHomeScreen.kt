package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val heroGreen = Color(0xFF004A3E)
private val heroGold = Color(0xFFE5C17A)
private val heroCream = Color(0xFFFFFAF0)

@Composable
internal fun PremiumHomeScreen(
    store: ProgressStore,
    repo: QuranRepository,
    onContinue: () -> Unit,
    onSurahList: () -> Unit,
    onLearn: () -> Unit,
    onWorship: () -> Unit,
    onShortSurah: (Int) -> Unit
) {
    val last = store.position()
    val surah = repo.surah(last.surah)
    Column(
        Modifier.fillMaxSize().background(heroCream).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(heroGreen, Color(0xFF06715A))))
                .padding(horizontal = 17.dp, vertical = 19.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                AnimatedCalligraphySeal("الله", sizeDp = 72)
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NUR", color = heroGold, fontWeight = FontWeight.Bold,
                        fontSize = 35.sp, letterSpacing = 4.sp)
                    Text("Kur’an ve İslam Rehberi", color = Color.White, fontSize = 12.sp)
                }
                AnimatedCalligraphySeal("محمد", sizeDp = 72)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ",
                color = Color(0xFFFFEBC0), fontFamily = RefFont,
                fontSize = 29.sp, lineHeight = 49.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
            Text("Oku • Dinle • Öğren • Yaşa",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center, color = Color(0xFFE0F1E8), fontSize = 12.sp)
        }
        Surface(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            color = Color(0xFF0B5A4D),
            shape = RoundedCornerShape(23.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(19.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bookmarks, null, tint = heroGold)
                    Spacer(Modifier.width(9.dp))
                    Text("KALDIĞIN YERDEN DEVAM ET",
                        color = heroGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(surah.turkishName + " Suresi", color = Color.White,
                    fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(last.ayah.toString() + ". ayet • Okumaya devam et",
                        modifier = Modifier.weight(1f), color = Color(0xFFDAEBE0), fontSize = 13.sp)
                    Icon(Icons.Default.PlayCircle, "Okumaya devam",
                        tint = heroGold, modifier = Modifier.size(35.dp))
                }
            }
        }
        Text("Keşfet ve öğren", color = heroGreen, fontSize = 22.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 17.dp))
        Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PremiumHomeTile("Kur’an-ı Kerim", "114 sure • 6.236 ayet",
                    Icons.Default.MenuBook, Modifier.weight(1f), onSurahList)
                PremiumHomeTile("İbadet Rehberi", "Namaz • Abdest • Dualar",
                    Icons.Default.Mosque, Modifier.weight(1f), onWorship)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PremiumHomeTile("Elifba Öğren", "Arapça harfleri keşfet",
                    Icons.Default.School, Modifier.weight(1f), onLearn)
                PremiumHomeTile("Çevrimdışı Dinle", "Hazır kısa sureler",
                    Icons.Default.Headphones, Modifier.weight(1f), { onShortSurah(1) })
            }
        }
        Surface(
            color = Color.White,
            border = BorderStroke(.8.dp, Color(0xFFE7D9B8)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)
        ) {
            Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Headphones, null, tint = heroGreen)
                    Spacer(Modifier.width(9.dp))
                    Text("Kısa sureler • Çevrimdışı kıraat",
                        color = heroGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("Fâtiha, İhlâs, Felak ve Nâs sesleri uygulamaya dahildir.",
                    color = Color(0xFF61716A), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.fillMaxWidth()) {
                    listOf("Fâtiha" to 1, "İhlâs" to 112,
                        "Felak" to 113, "Nâs" to 114).forEach { (title, number) ->
                        OutlinedButton(
                            onClick = { onShortSurah(number) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, heroGold)
                        ) {
                            Text(title, color = heroGreen, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
        Text(
            "Kur’an metni: Tanzil • Türkçe meal: Şaban Piriş / QuranEnc",
            color = Color(0xFF718077), fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PremiumHomeTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(onClick = onClick, modifier = modifier.height(152.dp),
        color = Color.White, shape = RoundedCornerShape(22.dp),
        border = BorderStroke(.8.dp, Color(0xFFE9DCBD)),
        shadowElevation = 1.dp) {
        Column(Modifier.fillMaxSize().padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Surface(color = Color(0xFFEDF6EC),
                shape = RoundedCornerShape(13.dp)) {
                Icon(icon, null, tint = heroGreen,
                    modifier = Modifier.padding(10.dp).size(25.dp))
            }
            Column {
                Text(title, color = heroGreen, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, maxLines = 2)
                Text(subtitle, color = Color(0xFF65736B), fontSize = 11.sp, maxLines = 2)
            }
        }
    }
}
