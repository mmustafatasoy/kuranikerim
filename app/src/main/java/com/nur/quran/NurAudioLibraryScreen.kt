package com.nur.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Functional library shortcuts: embedded short surahs and full Quran library. */
@Composable
internal fun NurAudioLibraryScreen(
    onOpenSurah: (Int) -> Unit,
    onAllSurahs: () -> Unit,
    onSettings: () -> Unit
) {
    val emerald = Color(0xFF065A4D)
    val gold = Color(0xFFECC47A)
    Column(Modifier.fillMaxSize().background(Color(0xFFFFFAF0))) {
        RefTopBar("Sesli Kütüphane", "Kur'an kıraati • Çevrimdışı dinle")
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = RoundedCornerShape(22.dp), color = emerald) {
                Column(
                    Modifier.fillMaxWidth().background(
                        Brush.horizontalGradient(listOf(
                            Color(0xFF043A32), Color(0xFF0B8475), Color(0xFF03695D)
                        ))
                    ).padding(18.dp)
                ) {
                    Icon(Icons.Default.Headphones, null, tint = gold,
                        modifier = Modifier.size(33.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Huzurlu dinlemeler", color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("Mishary Rashid Alafasy sesinden Fâtiha, İhlâs, Felak ve Nâs kayıtları uygulamaya gömülüdür.",
                        color = Color(0xFFE2F3EB), fontSize = 12.sp,
                        lineHeight = 19.sp)
                }
            }
            Text("Çevrimdışı hazır sureler",
                fontWeight = FontWeight.Bold, color = emerald, fontSize = 19.sp,
                modifier = Modifier.padding(top = 7.dp))
            listOf(
                Triple("Fâtiha Suresi", "7 ayet • 1. Sure", 1),
                Triple("İhlâs Suresi", "4 ayet • 112. Sure", 112),
                Triple("Felak Suresi", "5 ayet • 113. Sure", 113),
                Triple("Nâs Suresi", "6 ayet • 114. Sure", 114)
            ).forEachIndexed { index, entry ->
                Surface(
                    onClick = { onOpenSurah(entry.third) },
                    shape = RoundedCornerShape(16.dp),
                    color = listOf(
                        Color(0xFFDCF5E8), Color(0xFFFFF0C8),
                        Color(0xFFCDF4F1), Color(0xFFFFE2D9)
                    )[index],
                    border = BorderStroke(1.dp, Color(0xFFDEC48B))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.GraphicEq, null, tint = emerald,
                            modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(entry.first, color = emerald, fontSize = 16.sp,
                                fontWeight = FontWeight.Bold)
                            Text(entry.second + " • Çevrimdışı",
                                color = Color(0xFF527466), fontSize = 11.sp)
                        }
                        Icon(Icons.Default.PlayCircle, "Dinle", tint = emerald,
                            modifier = Modifier.size(32.dp))
                    }
                }
            }
            Text("Tüm surelerin kıraati", fontSize = 19.sp,
                color = emerald, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp))
            Surface(onClick = onAllSurahs, shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, gold), color = Color.White) {
                Row(Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, null, tint = emerald,
                        modifier = Modifier.size(29.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("114 sureyi görüntüle", color = emerald,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("İstediğin sureyi açıp 'Sureyi indir' ile telefona kaydedebilirsin.",
                            fontSize = 12.sp, lineHeight = 17.sp,
                            color = Color(0xFF66786B))
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = emerald)
                }
            }
            Surface(onClick = onSettings, shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF3E9D4)) {
                Row(Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, null, tint = emerald)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Ses ve uygulama ayarları",
                            fontWeight = FontWeight.Bold, color = emerald)
                        Text("Hafız seçimi, indirme ve oynatma seçenekleri",
                            color = Color(0xFF657668), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = emerald)
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}
