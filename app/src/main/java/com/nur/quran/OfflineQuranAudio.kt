package com.nur.quran

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext

/**
 * Recitation files are for personal/educational use under Al Quran Cloud terms.
 * Starter pack: Alafasy Fatiha (1..7), Ikhlas/Falaq/Nas (6222..6236).
 * Other surahs can be downloaded inside NUR; no Android document picker.
 */
internal object OfflineQuranAudio {
    fun starterAyah(globalAyah: Int, reciter: String): Boolean =
        reciter == "ar.alafasy" &&
            (globalAyah in 1..7 || globalAyah in 6222..6236)

    private fun location(context: Context, reciter: String, globalAyah: Int): File =
        File(context.filesDir, "quran_recitations/" + reciter + "/" + globalAyah + ".mp3")

    fun isReady(context: Context, reciter: String, globalAyah: Int): Boolean =
        starterAyah(globalAyah, reciter) ||
            location(context, reciter, globalAyah).let { it.isFile && it.length() > 1024L }

    fun mediaUri(context: Context, reciter: String, globalAyah: Int): String {
        val file = location(context, reciter, globalAyah)
        if (file.isFile && file.length() > 1024L) return Uri.fromFile(file).toString()
        if (starterAyah(globalAyah, reciter)) {
            return "asset:///quran_audio/alafasy/" + globalAyah + ".mp3"
        }
        return QuranAudioLinks.url(globalAyah, reciter)
    }

    fun readyCount(context: Context, surah: Surah, offset: Int, reciter: String): Int =
        (1..surah.ayahCount).count { n -> isReady(context, reciter, offset + n) }

    /** Coroutine-bound: cancelled download never leaves a half-usable file. */
    suspend fun downloadSurah(
        context: Context,
        surah: Surah,
        precedingAyahs: Int,
        reciter: String,
        onProgress: suspend (Int, Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        require(precedingAyahs >= 0 && precedingAyahs + surah.ayahCount <= 6236)
        var completed = 0
        onProgress(completed, surah.ayahCount)
        for (number in 1..surah.ayahCount) {
            coroutineContext.ensureActive()
            val global = precedingAyahs + number
            if (!isReady(context, reciter, global)) {
                val target = location(context, reciter, global)
                target.parentFile?.mkdirs()
                val temporary = File(target.parentFile, global.toString() + ".download")
                val connection = URL(QuranAudioLinks.url(global, reciter))
                    .openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 30000
                connection.instanceFollowRedirects = true
                try {
                    check(connection.responseCode == 200) {
                        "Ses dosyası indirilemedi (HTTP " + connection.responseCode + ")"
                    }
                    try {
                        connection.inputStream.use { input ->
                            temporary.outputStream().use { output ->
                                val buffer = ByteArray(32 * 1024)
                                while (true) {
                                    coroutineContext.ensureActive()
                                    val count = input.read(buffer)
                                    if (count < 0) break
                                    output.write(buffer, 0, count)
                                }
                            }
                        }
                        check(temporary.length() > 1024L) {
                            "Eksik ses dosyası: " + global
                        }
                        if (!temporary.renameTo(target)) {
                            temporary.copyTo(target, overwrite = true)
                            temporary.delete()
                        }
                    } finally {
                        temporary.delete()
                    }
                } finally {
                    connection.disconnect()
                }
            }
            completed++
            onProgress(completed, surah.ayahCount)
        }
    }
}
