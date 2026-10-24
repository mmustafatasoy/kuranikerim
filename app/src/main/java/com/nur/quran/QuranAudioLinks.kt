package com.nur.quran

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

/**
 * Al Quran Cloud ayah CDN: https://alquran.cloud/cdn
 * Stream only; the audio belongs to the recording's rightsholder.
 */
object QuranAudioLinks {
    fun url(globalAyah: Int, reciter: String = "ar.alafasy"): String {
        require(globalAyah in 1..6236) { "Global ayet indeksi 1..6236 olmalı" }
        val editions = mapOf(
            "ar.alafasy" to 128,
            "ar.sudais" to 192,
            "ar.husary" to 128,
            "ar.minshawi" to 128
        )
        val bitrate = requireNotNull(editions[reciter]) { "Unsupported reciter" }
        return "https://cdn.islamic.network/quran/audio/" +
            bitrate + "/" + reciter + "/" + globalAyah + ".mp3"
    }

    fun playlist(context: Context, surah: Surah, precedingAyahs: Int, reciter: String = "ar.alafasy"): List<MediaItem> {
        require(precedingAyahs >= 0 && precedingAyahs + surah.ayahCount <= 6236)
        return (1..surah.ayahCount).map { ayah ->
            val key = surah.number.toString() + ":" + ayah + ":" + reciter
            MediaItem.Builder()
                .setUri(OfflineQuranAudio.mediaUri(context, reciter, precedingAyahs + ayah))
                .setMediaId(key)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(surah.turkishName + " • " + ayah + ". ayet")
                        .setArtist(reciterLabel(reciter))
                        .build()
                )
                .build()
        }
    }
}
