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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val forest = Color(0xFF004E42)
private val bronze = Color(0xFFD7AE62)
private val ivory = Color(0xFFFFFAEE)

/** Actual offline instructional pages; no placeholder cards that pretend to work. */
internal data class WorshipLesson(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val steps: List<Pair<String, String>>,
    val note: String
)

private val lessons = listOf(
    WorshipLesson(
        "Abdest Nasıl Alınır?", "Temizlik ve hazırlık", Icons.Default.WaterDrop,
        listOf(
            "Niyet" to "İbadet için temizlenmeye niyet edilir; besmeleyle başlanır.",
            "Eller" to "Eller bileklere kadar yıkanır.",
            "Ağız ve burun" to "Ağıza ve burna su verilerek temizlenir.",
            "Yüz" to "Yüz tamamen yıkanır.",
            "Kollar" to "Önce sağ, sonra sol kol dirseklerle birlikte yıkanır.",
            "Başın mesh edilmesi" to "Islak elle baş mesh edilir.",
            "Kulak ve ense" to "Sünnet uygulamasında kulaklar ve ense mesh edilir.",
            "Ayaklar" to "Önce sağ, sonra sol ayak topuklarla birlikte yıkanır."
        ),
        "Kaynak: Diyanet Din İşleri Yüksek Kurulu, 'Abdest nedir ve nasıl alınır?' (12.07.2017). " +
            "Bu özet Hanefî geleneğinde yaygın anlatımı temel alır; mezhep ayrıntıları değişebilir."
    ),
    WorshipLesson(
        "Namaz Nasıl Kılınır?", "İki rekât örneği", Icons.Default.SelfImprovement,
        listOf(
            "Hazırlık" to "Abdestli, temiz bir yerde kıbleye yönelip uygun biçimde örtünün ve niyet edin.",
            "İftitah tekbiri" to "Allahu ekber diyerek namaza başlayın.",
            "Kıyam" to "Ayaktayken Sübhaneke, eûzü-besmele, Fâtiha ve ek sure okunur.",
            "Rükû" to "Rükûya eğilerek 'Sübhâne rabbiye'l-azîm' tesbihi söylenir.",
            "Doğrulma" to "Semiallahü limen hamideh; Rabbenâ leke'l-hamd denir.",
            "Secde" to "İki secde yapılır; her secdede 'Sübhâne rabbiye'l-a'lâ' denir.",
            "İkinci rekât" to "Doğrulup Fâtiha ve ek sure okunur; rükû ve secdeler tekrarlanır.",
            "Son oturuş" to "Tahiyyat, salavat ve dualar okunur; selâm verilir."
        ),
        "Bu bir giriş rehberidir. Farz, sünnet, rekât sayıları ve mezheplere göre ayrıntılar değişir. " +
            "Kaynak karşılaştırması: Diyanet 'Namaz İlmihali'."
    ),
    WorshipLesson(
        "Gusül Abdesti", "Boy abdesti", Icons.Default.Bathtub,
        listOf(
            "Niyet" to "Gusül için niyet edilir ve besmele çekilir.",
            "Ağız" to "Ağıza su alınarak içi iyice çalkalanır.",
            "Burun" to "Burna su alınarak içi temizlenir.",
            "Bütün beden" to "Vücutta kuru yer kalmayacak şekilde bütün beden yıkanır."
        ),
        "Hanefî fıkhında guslün farzları ağız, burun ve bütün bedeni yıkamaktır. " +
            "Diğer mezheplerde bazı ayrıntılar farklıdır."
    ),
    WorshipLesson(
        "Teyemmüm", "Su bulunmadığında temizlik", Icons.Default.Landscape,
        listOf(
            "Şartları öğren" to "Su bulunmaması veya kullanılmasının sağlık açısından sakıncalı olması gibi geçerli mazeretleri değerlendirin.",
            "Niyet" to "Teyemmüm için niyet edilir.",
            "Yüz" to "Temiz toprak cinsinden bir yüzeye eller temas ettirilip yüz mesh edilir.",
            "Kollar" to "Eller tekrar temas ettirilerek kollar dirseklerle birlikte mesh edilir."
        ),
        "Kaynak: Mâide 5:6 ve Diyanet temizlik/teyemmüm açıklamaları. Ayrıntılar mezhebe göre değişebilir."
    ),
    WorshipLesson(
        "Namazda Okunan Dualar", "Okunuş ve anlam", Icons.Default.AutoStories,
        listOf(
            "Sübhaneke" to "Sübhânekellâhümme ve bihamdik. Ve tebârekesmük. Ve teâlâ ceddük. Ve lâ ilâhe ğayrük.",
            "Anlamı" to "Allah'ım, sen eksikliklerden uzaksın; seni överim. Adın bereketli, şanın yücedir. Senden başka ilâh yoktur.",
            "Rükû tesbihi" to "Sübhâne rabbiye'l-azîm. (Yüce Rabbimi noksan sıfatlardan uzak tutarım.)",
            "Secde tesbihi" to "Sübhâne rabbiye'l-a'lâ. (En yüce Rabbimi noksan sıfatlardan uzak tutarım.)"
        ),
        "Okunuşlar öğrenmeye yardımcı Latin harfli aktarımlardır; Arapçanın bütün seslerini yansıtmayabilir. " +
            "Tilavet için doğrulanmış Arapça kaynak ve hafız kaydı kullanın."
    )
)

@Composable
internal fun WorshipGuideScreen(onOpenSurah: (Int) -> Unit) {
    var lesson by remember { mutableStateOf<WorshipLesson?>(null) }
    Column(Modifier.fillMaxSize().background(ivory)) {
        RefTopBar(
            if (lesson == null) "İbadet Rehberi" else "İbadet Öğren",
            if (lesson == null) "Namaz • Abdest • Dualar" else lesson!!.title,
            onBack = if (lesson == null) null else ({ lesson = null })
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (lesson == null) {
                Surface(color = forest, shape = RoundedCornerShape(23.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ",
                            color = bronze, fontFamily = RefFont, fontSize = 27.sp,
                            textAlign = TextAlign.Center, lineHeight = 44.sp,
                            style = TextStyle(textDirection = TextDirection.Rtl))
                        Text("İbadeti adım adım öğren", color = Color.White,
                            fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Rehber metinleri internetsiz kullanılabilir.",
                            color = Color(0xFFE3EFEB), fontSize = 12.sp)
                    }
                }
                lessons.forEach { item ->
                    Surface(onClick = { lesson = item }, color = Color.White,
                        shape = RoundedCornerShape(19.dp),
                        border = BorderStroke(.8.dp, Color(0xFFE9DEBF))) {
                        Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(13.dp), color = Color(0xFFEDF7EE)) {
                                Icon(item.icon, null, tint = forest,
                                    modifier = Modifier.padding(10.dp).size(28.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.title, color = forest, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(item.subtitle, color = Color(0xFF68736E), fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = forest)
                        }
                    }
                }
                Text("Namaz Sureleri • Hazır Çevrimdışı Dinleme",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = forest)
                listOf(
                    Triple("Fâtiha", 1, "7 ayet"),
                    Triple("İhlâs", 112, "4 ayet"),
                    Triple("Felak", 113, "5 ayet"),
                    Triple("Nâs", 114, "6 ayet")
                ).forEach { (name, number, verses) ->
                    Surface(onClick = { onOpenSurah(number) }, color = Color.White,
                        border = BorderStroke(.7.dp, bronze),
                        shape = RoundedCornerShape(15.dp)) {
                        Row(Modifier.fillMaxWidth().padding(13.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            RefBadge(number, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(name + " Suresi", color = forest, fontWeight = FontWeight.Bold)
                                Text(verses + " • Hafız kıraati", color = Color.Gray, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.PlayCircle, "Dinlemeye git", tint = forest)
                        }
                    }
                }
            } else {
                val item = lesson!!
                Surface(color = forest, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.fillMaxWidth().padding(19.dp)) {
                        Icon(item.icon, null, tint = bronze, modifier = Modifier.size(35.dp))
                        Spacer(Modifier.height(9.dp))
                        Text(item.title, color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text(item.subtitle, color = Color(0xFFD2E8DC), fontSize = 13.sp)
                    }
                }
                item.steps.forEachIndexed { idx, step ->
                    Surface(color = Color.White, shape = RoundedCornerShape(17.dp)) {
                        Row(Modifier.fillMaxWidth().padding(15.dp)) {
                            RefBadge(idx + 1, modifier = Modifier.size(34.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(step.first, color = forest, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(step.second, color = Color(0xFF45534C),
                                    fontSize = 14.sp, lineHeight = 21.sp)
                            }
                        }
                    }
                }
                Surface(color = Color(0xFFF1E9D5), shape = RoundedCornerShape(15.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Kaynak ve açıklama", color = forest, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text(item.note, color = Color(0xFF4D5C4B), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
                if (item.title.contains("Namaz")) {
                    Button(
                        onClick = { onOpenSurah(1) },
                        colors = ButtonDefaults.buttonColors(containerColor = forest),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Headphones, null)
                        Text(" Fâtiha'yı çevrimdışı dinle")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
