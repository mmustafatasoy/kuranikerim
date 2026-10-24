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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class RefReadingSettings(
    val arabicSize: Int = 42,
    val mealSize: Int = 16,
    val colored: Boolean = true,
    val mealVisible: Boolean = true,
    val followMeal: Boolean = true,
    val autoNext: Boolean = true,
    val reciter: String = "ar.alafasy"
)

internal fun ProgressStore.loadRefSettings(): RefReadingSettings =
    RefReadingSettings(arabicSize = fontSize(), mealSize = mealFontSize(),
        colored = coloredArabic(), mealVisible = showMeal(), followMeal = followMeal(),
        autoNext = autoNextAyah(), reciter = reciterId())

internal fun ProgressStore.persistRefSettings(settings: RefReadingSettings) {
    setFontSize(settings.arabicSize)
    setMealFontSize(settings.mealSize)
    setColoredArabic(settings.colored)
    setShowMeal(settings.mealVisible)
    setFollowMeal(settings.followMeal)
    setAutoNextAyah(settings.autoNext)
    setReciterId(settings.reciter)
}

internal val RefReciters = listOf(
    "ar.alafasy" to "Mishary Rashid Alafasy",
    "ar.sudais" to "Abdurrahman Sudais",
    "ar.husary" to "Mahmoud Khalil Al-Husary",
    "ar.minshawi" to "Muhammad Siddiq Al-Minshawi"
)
internal fun reciterLabel(id: String): String =
    RefReciters.firstOrNull { it.first == id }?.second ?: "Mishary Rashid Alafasy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReferenceAyahSettingsSheet(
    settings: RefReadingSettings,
    onUpdate: (RefReadingSettings) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest=onDismiss,
        containerColor=Color.White,
        contentColor=Color(0xFF15231E),
        dragHandle={ BottomSheetDefaults.DragHandle() },
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.93f).verticalScroll(rememberScrollState())
            .padding(horizontal=18.dp, vertical=5.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onClick=onDismiss) {Icon(Icons.Default.ArrowBack,"Geri")}
                Text("Ayet Ayarları",fontSize=20.sp,fontWeight=FontWeight.Bold,
                    modifier=Modifier.weight(1f),textAlign=TextAlign.Center)
                IconButton(onClick=onDismiss){Icon(Icons.Default.Close,"Kapat")}
            }
            Text("Görünüm",fontWeight=FontWeight.Bold,fontSize=17.sp)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                listOf("Standart","Güzel Hat","Kalın","Meşhur").forEachIndexed { i, label ->
                    Surface(
                        onClick={onUpdate(settings.copy(arabicSize=listOf(28,40,46,52)[i]))},
                        modifier=Modifier.weight(1f),shape=RoundedCornerShape(10.dp),
                        border=BorderStroke(1.dp,if(settings.arabicSize==listOf(28,40,46,52)[i])
                            RefGreen else Color(0xFFDEE6E0)),color=Color(0xFFF9FBF8)
                    ) {
                        Column(Modifier.padding(vertical=9.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Text("بِسْمِ",fontSize=(18+i*3).sp,fontFamily=RefFont,color=RefGreen,
                                style=TextStyle(textDirection=TextDirection.Rtl))
                            Text(label,fontSize=10.sp,color=RefDark)
                        }
                    }
                }
            }
            Text("Arapça Yazı Boyutu",fontSize=15.sp,fontWeight=FontWeight.Medium)
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("A",fontSize=15.sp)
                Slider(value=settings.arabicSize.toFloat(),onValueChange={
                    onUpdate(settings.copy(arabicSize=it.toInt()))
                },valueRange=24f..52f,modifier=Modifier.weight(1f))
                Text("A",fontSize=26.sp)
            }
            Text("Meal Yazı Boyutu",fontSize=15.sp,fontWeight=FontWeight.Medium)
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("A",fontSize=15.sp)
                Slider(value=settings.mealSize.toFloat(),onValueChange={
                    onUpdate(settings.copy(mealSize=it.toInt()))
                },valueRange=12f..25f,modifier=Modifier.weight(1f))
                Text("A",fontSize=26.sp)
            }
            HorizontalDivider()
            RefSettingToggle("Tecvid Renkleri",
                "Şimdilik çok renkli görünüm; kurala dayalı tecvid işaretlemesi değildir.",
                settings.colored){onUpdate(settings.copy(colored=it))}
            RefSettingToggle("Türkçe Meal Göster","Her ayetin Türkçe anlamı",settings.mealVisible){
                onUpdate(settings.copy(mealVisible=it))
            }
            RefSettingToggle("Okuma Sırasında Meal Takibi",
                "Okunan ayetin Türkçe meali kırmızı vurgulanır.",
                settings.followMeal){onUpdate(settings.copy(followMeal=it))}
            Row(Modifier.fillMaxWidth().padding(vertical=7.dp),verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)) {
                    Text("Kelimeye Dokunarak Dinle",fontSize=14.sp,fontWeight=FontWeight.Medium)
                    Text("Doğrulanmış kelime ses kayıtları henüz eklenmedi.",
                        fontSize=11.sp,color=Color.Gray)
                }
                Switch(checked=false,onCheckedChange={},enabled=false)
            }
            HorizontalDivider()
            Text("Ses Seçimi",fontWeight=FontWeight.Bold,fontSize=17.sp)
            RefReciters.forEach { (id,name) ->
                Row(Modifier.fillMaxWidth().clickable {
                    onUpdate(settings.copy(reciter=id))
                }.padding(vertical=5.dp),verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle,null,tint=RefGreen,modifier=Modifier.size(35.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(name,modifier=Modifier.weight(1f),fontSize=14.sp)
                    RadioButton(selected=settings.reciter==id,onClick={
                        onUpdate(settings.copy(reciter=id))
                    })
                }
                HorizontalDivider(color=Color(0xFFF0F0EB))
            }
            HorizontalDivider()
            Text("Çalma Ayarları",fontWeight=FontWeight.Bold,fontSize=17.sp)
            RefSettingToggle("Otomatik Sonraki Ayet",
                "Kapalıysa seçilen ayetten sonra oynatma durur.",
                settings.autoNext){onUpdate(settings.copy(autoNext=it))}
            Surface(color=Color(0xFFF1F7F3),shape=RoundedCornerShape(13.dp)) {
                Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Default.Download,null,tint=RefGreen)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Hazır çevrimdışı kıraat",color=RefDark,fontWeight=FontWeight.Bold,fontSize=14.sp)
                        Text("Fâtiha, İhlâs, Felak ve Nâs yüklü. Diğer sureler için oynatıcının 'Sureyi indir' simgesine bas.",
                            color=RefGreen,fontSize=11.sp)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun RefSettingToggle(
    title: String,subtitle: String,checked:Boolean,onChange:(Boolean)->Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical=7.dp),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title,fontSize=14.sp,fontWeight=FontWeight.Medium)
            Text(subtitle,fontSize=11.sp,color=Color.Gray)
        }
        Switch(checked=checked,onCheckedChange=onChange)
    }
}
