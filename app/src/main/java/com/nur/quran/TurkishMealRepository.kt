package com.nur.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Verse-by-verse Turkish translation by Şaban Piriş, QuranEnc (turkish_shaban).
 * https://quranenc.com/tr/browse/turkish_shaban
 * API: https://quranenc.com/api/v1/translation/sura/turkish_shaban/114
 *
 * Diyanet tr.diyanet combines translations of some groups of ayahs (Nâs 1–6),
 * and cannot be used as one-meaning-per-ayah in this reader.
 * Never read the OLD tr.diyanet-v1 cache.
 */
class TurkishMealRepository(private val context: Context) {
    companion object {
        const val PROVIDER = "QuranEnc.com"
        const val AUTHOR = "Şaban Piriş"
        private const val SOURCE_KEY = "turkish_shaban"
        private const val SOURCE_URL = "https://quranenc.com/api/v1/translation/sura/"
        private const val CACHE_KEY = "quranenc-turkish-shaban-v1"
    }

    suspend fun surah(number: Int, expected: Int): List<String> =
        withContext(Dispatchers.IO) {
            require(number in 1..114 && expected > 0)
            val folder = File(context.filesDir, "meal/$CACHE_KEY")
            val cached = File(folder, "$number.json")
            runCatching { decode(cached.readText(Charsets.UTF_8), number, expected) }
                .getOrNull()?.let { return@withContext it }

            val connection = URL("$SOURCE_URL$SOURCE_KEY/$number")
                .openConnection() as HttpURLConnection
            connection.connectTimeout = 12000
            connection.readTimeout = 18000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "NurQuran-Android/0.5")
            try {
                check(connection.responseCode == HttpURLConnection.HTTP_OK) {
                    "QuranEnc meali alınamadı (HTTP " + connection.responseCode + ")"
                }
                val raw = connection.inputStream.bufferedReader(Charsets.UTF_8).use {
                    it.readText()
                }
                val verified = decode(raw, number, expected)
                folder.mkdirs()
                val temp = File(folder, "$number.part")
                temp.writeText(raw, Charsets.UTF_8)
                if (!temp.renameTo(cached)) {
                    temp.copyTo(cached, overwrite = true)
                    temp.delete()
                }
                verified
            } finally {
                connection.disconnect()
            }
        }

    /**
     * QuranEnc returns either a root array or {"result":[...]}.
     * Only an exact sequence of ayahs for the requested surah is accepted.
     * A grouped whole-surah translation repeated for every ayah is rejected.
     */
    private fun decode(json: String, number: Int, expected: Int): List<String> {
        val trimmed = json.trimStart()
        val verses: JSONArray = if (trimmed.startsWith("[")) {
            JSONArray(json)
        } else {
            val obj = JSONObject(json)
            obj.optJSONArray("result") ?: obj.optJSONArray("data")
            ?: error("QuranEnc formatı değişmiş: ayet listesi bulunamadı")
        }
        require(verses.length() == expected) { "Meal ayet sayısı uyuşmuyor" }
        val contents = (0 until expected).map { idx ->
            val row = verses.getJSONObject(idx)
            require(row.get("sura").toString().toInt() == number) {
                "Başka sureye ait meal"
            }
            require(row.get("aya").toString().toInt() == idx + 1) {
                "Meal / ayet numarası eşleşmiyor"
            }
            row.getString("translation").trim().also { require(it.isNotBlank()) }
        }
        AyahMealIntegrity.check(contents, expected)
        return contents
    }
}
