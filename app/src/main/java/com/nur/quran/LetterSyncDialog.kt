package com.nur.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A live playback time sampled from Media3, not time inferred from text. */
data class VerseAudioClock(
    val surah: Int = 0,
    val ayah: Int = 0,
    val reciter: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isPlaying: Boolean = false
)

/**
 * Listener marks the start of each audible letter in the same reciter/ayah.
 * Only a complete, monotonically timed sequence is stored and highlighted.
 * When there is no map the whole ayah is indicated, not fake letter timing.
 */
@Composable
fun LetterSyncDialog(
    surah: Int,
    ayah: Int,
    arabic: String,
    reciter: String,
    audio: VerseAudioClock,
    previouslyAligned: Boolean,
    onReplay: () -> Unit,
    onSave: (List<LetterCue>) -> Boolean,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val glyphs = remember(arabic) { ArabicLetterTimeline.glyphs(arabic) }
    val starts = remember(surah, ayah, reciter) { mutableStateListOf<Long>() }
    var message by remember { mutableStateOf<String?>(null) }
    val matching = audio.surah == surah && audio.ayah == ayah && audio.reciter == reciter
    val next = starts.size

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sesle harf eşleştir • " + surah + ":" + ayah, fontSize = 19.sp) },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Harfleri sesle eşleştirme kaydı: hafızı dinlerken duyduğun " +
                        "her yeni harfin başında 'İşaretle'ye bas. " +
                        "Harekeler ait oldukları harfle beraber işaretlenir.",
                    fontSize = 13.sp
                )
                Text("Kayıt yalnızca " + reciterLabel(reciter) + " için geçerli.",
                    fontSize = 12.sp, color = RefGreen)
                if (previouslyAligned) {
                    Text("Bu ayette tamamlanmış bir zamanlama bulunuyor.",
                        fontSize = 12.sp, color = RefGreen)
                }
                LinearProgressIndicator(
                    progress = { if (glyphs.isEmpty()) 0f else next.toFloat() / glyphs.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("İşaretlenen harf: " + next + " / " + glyphs.size, fontSize = 13.sp)
                Surface(color = Color(0xFFFFF4DE),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (next < glyphs.size) glyphs[next].text else "✓",
                            fontFamily = RefFont, fontSize = 58.sp, color = RefRed,
                            textAlign = TextAlign.Center,
                            style = TextStyle(textDirection = TextDirection.Rtl),
                            lineHeight = 82.sp
                        )
                        Text(if (next < glyphs.size) "Sıradaki duyulacak harf"
                            else "Son harf işaretlendi; bitişi kaydet",
                            fontSize = 12.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Kıraat: " + if (!matching) "Ayet sesini başlat"
                        else if (audio.isPlaying) "Çalıyor • " + audio.positionMs + " ms"
                        else "Duraklatıldı • " + audio.positionMs + " ms",
                        fontSize = 12.sp, modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {
                        starts.clear()
                        message = null
                        onReplay()
                    }) {
                        Icon(Icons.Default.RestartAlt, null, modifier = Modifier.size(16.dp))
                        Text(" Baştan")
                    }
                }
                Button(
                    onClick = {
                        if (!matching) {
                            message = "Önce bu ayetin sesini başlat."
                        } else if (next < glyphs.size) {
                            val ms = audio.positionMs
                            if (!audio.isPlaying) {
                                message = "Hafız okurken işaretle."
                            } else if (starts.isNotEmpty() && ms <= starts.last()) {
                                message = "Bir önceki işaretten sonraki sesi seç."
                            } else {
                                starts.add(ms)
                                message = null
                            }
                        } else {
                            val cues = LetterCueMath.fromRecordedStarts(
                                starts.toList(), audio.positionMs
                            )
                            if (cues == null) message = "Bitiş zamanı son harften sonra olmalı."
                            else if (onSave(cues)) {
                                message = "Harf zamanları telefona kaydedildi."
                                onDismiss()
                            } else message = "Kayıt başarısız oldu."
                        }
                    },
                    enabled = matching && (audio.isPlaying || next == glyphs.size),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (next < glyphs.size) "ŞİMDİ OKUNAN HARFİ İŞARETLE"
                        else "OKUMA BİTTİ • KAYDET")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = { if (starts.isNotEmpty()) starts.removeAt(starts.lastIndex) },
                        enabled = starts.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Undo, null, modifier = Modifier.size(17.dp))
                        Text(" Geri al")
                    }
                    OutlinedButton(
                        onClick = { onDelete(); starts.clear(); message = "Eski kayıt silindi." },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(17.dp))
                        Text(" Kaydı sil")
                    }
                }
                if (message != null) Text(message ?: "", color = RefRed, fontSize = 12.sp)
                Text("Bu manuel eşleştirmedir. Doğru işaretlenmiş harfler, " +
                    "aynı hafızın aynı ayet kaydında gerçek oynatma zamanına göre " +
                    "kırmızı görünür. Diğer hafızlara kopyalanmaz.",
                    color = Color.Gray, fontSize = 11.sp)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Kapat") } }
    )
}
