package com.nur.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** User chooses when to download and install: never a background silent install. */
@Composable
internal fun NurUpdateDialog(
    manager: NurUpdateManager,
    onDismiss: () -> Unit,
    onCheckAgain: () -> Unit
) {
    val release = manager.release
    val phase = manager.phase
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFFBF2),
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Icon(Icons.Default.SystemUpdate, "Güncelleme", tint = RefGreen)
                Text("NUR Güncellemeleri", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(manager.message, color = Color(0xFF234A3D))
                if (release != null) {
                    Text("Yeni sürüm: " + release.tag,
                        color = RefGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("İndirme boyutu yaklaşık " +
                        String.format("%.1f", release.bytes / 1048576.0) + " MB",
                        fontSize = 13.sp, color = Color(0xFF58695D))
                    if (release.notes.isNotBlank()) {
                        Text(release.notes.take(700), fontSize = 12.sp,
                            color = Color(0xFF3D5548))
                    }
                }
                if (phase == UpdatePhase.CHECKING || phase == UpdatePhase.DOWNLOADING ||
                    phase == UpdatePhase.VERIFYING) {
                    LinearProgressIndicator(Modifier.fillMaxWidth(), color = RefGreen)
                }
                if (phase == UpdatePhase.DOWNLOADED ||
                    phase == UpdatePhase.PERMISSION_REQUIRED) {
                    Text("Android kurulumu açıldığında yükleme için ayrıca onay vermen gerekir. " +
                        "Mevcut uygulamanın üzerine kurmak için aynı imza anahtarı kullanılmalıdır.",
                        fontSize = 12.sp, color = Color(0xFF6E5A2B))
                }
            }
        },
        confirmButton = {
            when (phase) {
                UpdatePhase.READY -> Button(
                    onClick = { manager.beginDownload() },
                    colors = ButtonDefaults.buttonColors(containerColor = RefGreen)
                ) {
                    Icon(Icons.Default.Download, null, Modifier.size(18.dp))
                    Text(" İndir")
                }
                UpdatePhase.DOWNLOADED, UpdatePhase.PERMISSION_REQUIRED -> Button(
                    onClick = { manager.requestInstall() },
                    colors = ButtonDefaults.buttonColors(containerColor = RefGreen)
                ) {
                    Icon(Icons.Default.InstallMobile, null, Modifier.size(18.dp))
                    Text(" Kur")
                }
                UpdatePhase.IDLE, UpdatePhase.NO_UPDATE, UpdatePhase.ERROR -> Button(
                    onClick = onCheckAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = RefGreen)
                ) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(18.dp))
                    Text(" Tekrar kontrol et")
                }
                else -> {}
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}
