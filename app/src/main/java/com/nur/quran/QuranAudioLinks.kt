package com.nur.quran

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

/**
 * Al Quran Cloud ayah CDN: https://alquran.cloud/cdn
 * Stream only; the audio belongs to the recording's rightsholder.
 */
object QuranAudioLinks {
    fun url(globalAyah: Int): String {
        require(globalAyah in 1..6236) { "Global ayet indeksi 1..6236 olmalı" }
        return "https://cdn.islamic.network/quran/audio/64/ar.alafasy/" + globalAyah + ".mp3"
    }

    fun playlist(surah: Surah, precedingAyahs: Int): List<MediaItem> {
        require(precedingAyahs >= 0 && precedingAyahs + surah.ayahCount <= 6236)
        return (1..surah.ayahCount).map { ayah ->
            val key = surah.number.toString() + ":" + ayah
            MediaItem.Builder()
                .setUri(url(precedingAyahs + ayah))
                .setMediaId(key)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(surah.turkishName + " • " + ayah + ". ayet")
                        .setArtist("Mishary Rashid Alafasy")
                        .build()
                )
                .build()
        }
    }
}
