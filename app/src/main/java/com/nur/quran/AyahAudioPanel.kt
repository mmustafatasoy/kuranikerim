package com.nur.quran

import android.content.ComponentName
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

private val AudioGold = Color(0xFFE8CB92)
private val AudioGreen = Color(0xFF2B5547)
private val AudioDark = Color(0xFF0B2823)

/** A MediaController connected to an independent foreground-capable MediaSessionService. */
@Composable
fun AyahAudioPanel(
    surah: Surah,
    globalAyahOffset: Int,
    store: ProgressStore,
    dark: Boolean,
    requestedAyah: Int,
    playNonce: Int,
    onAyahChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    val currentCallback by rememberUpdatedState(onAyahChanged)
    var controller by remember(surah.number) { mutableStateOf<MediaController?>(null) }
    var playing by remember(surah.number) { mutableStateOf(false) }
    var currentAyah by remember(surah.number) { mutableIntStateOf(0) }
    var repeating by remember(surah.number) { mutableStateOf(false) }
    var error by remember(surah.number) { mutableStateOf<String?>(null) }

    fun refresh(mediaController: MediaController) {
        playing = mediaController.isPlaying
        repeating = mediaController.repeatMode == Player.REPEAT_MODE_ONE
        val id = mediaController.currentMediaItem?.mediaId ?: return
        val parts = id.split(":")
        val verse = if (parts.size == 2 && parts[0] == surah.number.toString())
            parts[1].toIntOrNull() else null
        if (verse != null && verse in 1..surah.ayahCount) {
            currentAyah = verse
            currentCallback(verse)
        } else {
            currentAyah = 0
        }
    }

    DisposableEffect(surah.number) {
        var disposed = false
        var attached: Player.Listener? = null
        var attachedController: MediaController? = null
        val token = SessionToken(
            context, ComponentName(context, QuranPlaybackService::class.java)
        )
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            try {
                val player = future.get()
                if (!disposed) {
                    val listener = object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            playing = isPlaying
                        }
                        override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                            refresh(player)
                        }
                        override fun onRepeatModeChanged(repeatMode: Int) {
                            repeating = repeatMode == Player.REPEAT_MODE_ONE
                        }
                        override fun onPlayerError(exception: PlaybackException) {
                            playing = false
                            error = "Ses yüklenemedi. İnternet bağlantını kontrol et veya yerel MP3 seç."
                        }
                    }
                    player.addListener(listener)
                    attached = listener
                    attachedController = player
                    controller = player
                    refresh(player)
                }
            } catch (_: Exception) {
                if (!disposed) error = "Ses servisine bağlanılamadı. Uygulamayı tekrar aç."
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            disposed = true
            attached?.let { listener -> attachedController?.removeListener(listener) }
            MediaController.releaseFuture(future)
        }
    }

    fun playFrom(verseNumber: Int) {
        val player = controller ?: run {
            error = "Ses oynatıcı hazırlanıyor, yeniden deneyin."
            return
        }
        if (verseNumber !in 1..surah.ayahCount) return
        try {
            error = null
            player.setMediaItems(
                QuranAudioLinks.playlist(surah, globalAyahOffset),
                verseNumber - 1,
                0L
            )
            player.prepare()
            player.play()
            currentAyah = verseNumber
            store.savePosition(surah.number, verseNumber)
            currentCallback(verseNumber)
        } catch (_: Exception) {
            error = "Kıraat başlatılamadı. İnterneti kontrol edin."
        }
    }

    LaunchedEffect(playNonce, controller) {
        if (playNonce > 0 && controller != null) playFrom(requestedAyah)
    }

    val belongsToSurah = controller?.currentMediaItem?.mediaId
        ?.startsWith(surah.number.toString() + ":") == true
    val textColor = if (dark) Color.White else AudioDark
    Surface(
        color = if (dark) AudioGreen else Color(0xFFE3EEDF),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Headphones, "Sesli kıraat", tint = if (dark) AudioGold else AudioDark)
                Spacer(Modifier.padding(4.dp))
                Column(Modifier.weight(1f)) {
                    Text("Sesli Kur’an • Mishary Alafasy", fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, color = textColor)
                    Text(
                        if (currentAyah > 0) "Okunan ayet: " + currentAyah
                        else "İnternetten gerçek hafız kaydı",
                        fontSize = 11.sp, color = textColor
                    )
                }
                Button(onClick = { playFrom(1) }, enabled = controller != null) {
                    Text("Sureyi oku")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    enabled = belongsToSurah && controller?.hasPreviousMediaItem() == true,
                    onClick = { controller?.seekToPreviousMediaItem() }
                ) { Icon(Icons.Default.SkipPrevious, "Önceki ayet", tint = textColor) }
                IconButton(
                    enabled = controller != null,
                    onClick = {
                        val p = controller ?: return@IconButton
                        if (!belongsToSurah || p.mediaItemCount == 0)
                            playFrom(store.position().takeIf { it.surah == surah.number }?.ayah ?: 1)
                        else if (p.isPlaying) p.pause() else p.play()
                    }
                ) { Icon(if (playing && belongsToSurah) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (playing) "Duraklat" else "Oynat", tint = if (dark) AudioGold else AudioDark) }
                IconButton(
                    enabled = belongsToSurah && controller?.hasNextMediaItem() == true,
                    onClick = { controller?.seekToNextMediaItem() }
                ) { Icon(Icons.Default.SkipNext, "Sonraki ayet", tint = textColor) }
                IconButton(
                    enabled = belongsToSurah,
                    onClick = {
                        controller?.repeatMode =
                            if (repeating) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE
                    }
                ) { Icon(Icons.Default.Repeat, "Ayeti tekrarla",
                    tint = if (repeating) AudioGold else textColor) }
            }
            Text(
                "▶ ile herhangi bir ayeti başlat. Ayet bitince sıradakine geçer. " +
                    "Ekran kilitliyken de dinleyebilirsin.",
                color = textColor, fontSize = 11.sp
            )
            if (error != null) Text(error ?: "", color = Color(0xFFF5B19F), fontSize = 12.sp)
        }
    }
}
