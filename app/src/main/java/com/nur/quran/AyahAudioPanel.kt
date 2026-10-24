package com.nur.quran

import android.content.ComponentName
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private val BarGreen = Color(0xFF00574A)
private val BarGold = Color(0xFFEBCB85)
private val BarOrange = Color(0xFFFF6236)

@Composable
fun AyahAudioPanel(
    surah: Surah,
    globalAyahOffset: Int,
    store: ProgressStore,
    dark: Boolean,
    requestedAyah: Int,
    playNonce: Int,
    onAyahChanged: (Int) -> Unit,
    onPlayingChanged: (Boolean) -> Unit = {},
    reciterId: String = "ar.alafasy",
    autoNext: Boolean = true,
    onSettings: () -> Unit = {},
    onLocalAudio: () -> Unit = {},
    onAudioClock: (VerseAudioClock) -> Unit = {}
) {
    val context = LocalContext.current
    val currentAyahCallback by rememberUpdatedState(onAyahChanged)
    val playingCallback by rememberUpdatedState(onPlayingChanged)
    val clockCallback by rememberUpdatedState(onAudioClock)
    var controller by remember(surah.number) { mutableStateOf<MediaController?>(null) }
    var isPlaying by remember(surah.number) { mutableStateOf(false) }
    var currentAyah by remember(surah.number) { mutableIntStateOf(0) }
    var repeating by remember(surah.number) { mutableStateOf(false) }
    var error by remember(surah.number) { mutableStateOf<String?>(null) }
    var positionMs by remember(surah.number) { mutableLongStateOf(0L) }
    var durationMs by remember(surah.number) { mutableLongStateOf(0L) }
    var movingSlider by remember(surah.number) { mutableStateOf(false) }
    var sliderPosition by remember(surah.number) { mutableFloatStateOf(0f) }
    var currentSpeed by remember(surah.number) { mutableFloatStateOf(store.speed()) }

    fun refresh(player: MediaController) {
        isPlaying = player.isPlaying
        playingCallback(player.isPlaying)
        repeating = player.repeatMode == Player.REPEAT_MODE_ONE
        val key = player.currentMediaItem?.mediaId ?: return
        val parts = key.split(":")
        val ayah = if(parts.size>=2 && parts[0]==surah.number.toString())
            parts[1].toIntOrNull() else null
        if(ayah!=null && ayah in 1..surah.ayahCount) {
            currentAyah = ayah
            currentAyahCallback(ayah)
        } else currentAyah = 0
    }

    DisposableEffect(surah.number) {
        var released=false
        var listener:Player.Listener?=null
        var attached:MediaController?=null
        val token=SessionToken(context,ComponentName(context,QuranPlaybackService::class.java))
        val future=MediaController.Builder(context,token).buildAsync()
        future.addListener({
            try {
                val p=future.get()
                if(!released) {
                    val l=object: Player.Listener {
                        override fun onIsPlayingChanged(value:Boolean) {
                            isPlaying=value
                            playingCallback(value)
                        }
                        override fun onMediaItemTransition(item:MediaItem?,reason:Int) {
                            refresh(p)
                        }
                        override fun onRepeatModeChanged(value:Int) {
                            repeating=value==Player.REPEAT_MODE_ONE
                        }
                        override fun onPlayerError(exception:PlaybackException) {
                            error="Ses yüklenemedi. Bağlantıyı veya seçilen hafızı kontrol et."
                            isPlaying=false
                            playingCallback(false)
                        }
                    }
                    listener=l
                    attached=p
                    p.addListener(l)
                    controller=p
                    p.setPlaybackSpeed(currentSpeed)
                    refresh(p)
                }
            } catch(_:Exception) {
                if(!released)error="Ses oynatıcısına bağlanılamadı."
            }
        },ContextCompat.getMainExecutor(context))
        onDispose {
            released=true
            listener?.let {attached?.removeListener(it)}
            MediaController.releaseFuture(future)
        }
    }

    fun playFrom(number:Int) {
        val p=controller ?: run {error="Oynatıcı hazırlanıyor.";return}
        if(number !in 1..surah.ayahCount)return
        try {
            error=null
            val items=QuranAudioLinks.playlist(surah,globalAyahOffset,reciterId)
            if(autoNext) p.setMediaItems(items,number-1,0L)
            else p.setMediaItem(items[number-1],0L)
            p.prepare()
            p.play()
            currentAyah=number
            store.savePosition(surah.number,number)
            currentAyahCallback(number)
            isPlaying=true
            playingCallback(true)
        } catch(_:Exception) {
            error="Kıraat başlatılamadı."
            playingCallback(false)
        }
    }
    LaunchedEffect(playNonce,controller) {
        if(playNonce>0 && controller!=null)playFrom(requestedAyah)
    }
    LaunchedEffect(controller,isPlaying) {
        while(isActive) {
            val p=controller
            if(p!=null) {
                val d=p.duration
                durationMs=if(d==C.TIME_UNSET||d<1L)0L else d
                positionMs=p.currentPosition.coerceAtLeast(0L)
                val idParts=p.currentMediaItem?.mediaId?.split(":").orEmpty()
                val currentNumber=if(idParts.size>=2 && idParts[0]==surah.number.toString())
                    idParts[1].toIntOrNull() ?: 0 else 0
                val currentReciter=if(idParts.size==3)idParts[2] else ""
                clockCallback(
                    VerseAudioClock(
                        surah=surah.number, ayah=currentNumber,
                        reciter=currentReciter, positionMs=positionMs,
                        durationMs=durationMs, isPlaying=p.isPlaying
                    )
                )
            }
            delay(if(p?.isPlaying==true) 70L else 250L)
        }
    }

    val belongs = controller?.currentMediaItem?.mediaId
        ?.startsWith(surah.number.toString()+":")==true
    val enabled = controller!=null
    Surface(
        modifier=Modifier.fillMaxWidth().padding(horizontal=5.dp,vertical=2.dp),
        color=BarGreen,shape=RoundedCornerShape(topStart=18.dp,topEnd=18.dp,bottomStart=7.dp,bottomEnd=7.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=7.dp),
            verticalArrangement=Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(reciterLabel(reciterId),fontSize=12.sp,color=Color.White,
                        fontWeight=FontWeight.Bold,maxLines=1)
                    Text(surah.turkishName+" Suresi • Ayet "+
                        (if(currentAyah>0)currentAyah else requestedAyah).toString()+
                        "/"+surah.ayahCount.toString(),
                        fontSize=11.sp,color=Color(0xFFDFEFE6),maxLines=1)
                }
                IconButton(enabled=enabled,onClick={
                    playFrom((if(currentAyah>0)currentAyah else requestedAyah)-1)
                }) {
                    Icon(Icons.Default.SkipPrevious,"Önceki ayet",
                        tint=if(enabled)Color.White else Color.LightGray)
                }
                IconButton(enabled=enabled,onClick={
                    val p=controller ?: return@IconButton
                    if(!belongs || p.mediaItemCount==0) playFrom(
                        store.position().takeIf{it.surah==surah.number}
                            ?.ayah?.coerceIn(1,surah.ayahCount) ?:1)
                    else if(p.isPlaying)p.pause()
                    else {
                        if(p.playbackState==Player.STATE_ENDED)playFrom(currentAyah.coerceAtLeast(1))
                        else p.play()
                    }
                }) {
                    Icon(if(isPlaying && belongs) Icons.Default.PauseCircle
                         else Icons.Default.PlayCircle,
                        if(isPlaying)"Duraklat" else "Oynat",
                        tint=BarOrange, modifier=Modifier.size(44.dp))
                }
                IconButton(enabled=enabled,onClick={
                    playFrom((if(currentAyah>0)currentAyah else 0)+1)
                }) {
                    Icon(Icons.Default.SkipNext,"Sonraki ayet",
                        tint=if(enabled)Color.White else Color.LightGray)
                }
                IconButton(enabled=enabled,onClick={
                    val p=controller ?: return@IconButton
                    p.repeatMode=if(repeating)Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE
                }) {
                    Icon(Icons.Default.Repeat,"Ayeti tekrarla",
                        tint=if(repeating)BarGold else Color.White)
                }
            }
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Text(timeString(positionMs),fontSize=10.sp,color=Color.White)
                Slider(value=if(movingSlider)sliderPosition else
                    (if(durationMs>0L)(positionMs.toFloat()/durationMs).coerceIn(0f,1f) else 0f),
                    onValueChange={movingSlider=true;sliderPosition=it},
                    onValueChangeFinished={
                        controller?.let {p->
                            if(durationMs>0L)p.seekTo((sliderPosition*durationMs).toLong())
                        }
                        movingSlider=false
                    },
                    enabled=durationMs>0, valueRange=0f..1f,
                    modifier=Modifier.weight(1f).height(25.dp),
                    colors=SliderDefaults.colors(thumbColor=BarOrange,activeTrackColor=BarOrange,
                        inactiveTrackColor=Color(0xFF569386)))
                Text(timeString(durationMs),fontSize=10.sp,color=Color.White)
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly,
                verticalAlignment=Alignment.CenterVertically) {
                RefPlayerAction(Icons.Default.Repeat,"Tekrar"){
                    val p=controller
                    if(p!=null)p.repeatMode=if(repeating)
                        Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE
                }
                RefPlayerAction(Icons.Default.Speed,
                    currentSpeed.toString()+"x") {
                    currentSpeed=when(currentSpeed) {
                        1f->1.25f
                        1.25f->1.5f
                        1.5f->.75f
                        else->1f
                    }
                    store.setSpeed(currentSpeed)
                    controller?.setPlaybackSpeed(currentSpeed)
                }
                RefPlayerAction(Icons.Default.Settings,"Ayet Ayarları",onSettings)
                RefPlayerAction(Icons.Default.FolderOpen,"Çevrimdışı",onLocalAudio)
            }
            if(error!=null)Text(error ?: "",color=Color(0xFFFFC6AA),fontSize=11.sp)
        }
    }
}

private fun timeString(ms:Long):String {
    val seconds=(ms/1000L).coerceAtLeast(0L)
    return (seconds/60).toString().padStart(2,'0')+":"+
        (seconds%60).toString().padStart(2,'0')
}

@Composable
private fun RefPlayerAction(icon:androidx.compose.ui.graphics.vector.ImageVector,
    label:String,onClick:()->Unit) {
    TextButton(onClick=onClick,contentPadding=PaddingValues(horizontal=2.dp,vertical=1.dp)) {
        Column(horizontalAlignment=Alignment.CenterHorizontally) {
            Icon(icon,label,tint=Color.White,modifier=Modifier.size(18.dp))
            Text(label,fontSize=9.sp,color=Color.White,maxLines=1)
        }
    }
}
