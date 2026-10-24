package com.nur.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Turkish Diyanet edition via Al Quran Cloud; private offline cache, no AI translation. */
class TurkishMealRepository(private val context: Context) {
    suspend fun surah(number: Int, expected: Int): List<String> =
        withContext(Dispatchers.IO) {
            require(number in 1..114 && expected > 0)
            val folder = File(context.filesDir, "meal/tr.diyanet-v1")
            val cached = File(folder, number.toString() + ".json")
            runCatching { parse(cached.readText(Charsets.UTF_8), expected) }
                .getOrNull()?.let { return@withContext it }

            val url = URL("https://api.alquran.cloud/v1/surah/" + number + "/tr.diyanet")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 12000
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")
            try {
                check(connection.responseCode == 200) { "Meal sunucusuna ulaşılamadı" }
                val raw = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parse(raw, expected)
                folder.mkdirs()
                val temporary = File(folder, number.toString() + ".tmp")
                temporary.writeText(raw, Charsets.UTF_8)
                if (!temporary.renameTo(cached)) {
                    temporary.copyTo(cached, overwrite = true)
                    temporary.delete()
                }
                parsed
            } finally { connection.disconnect() }
        }

    private fun parse(text: String, count: Int): List<String> {
        val root = JSONObject(text)
        require(root.optInt("code") == 200)
        val data = root.getJSONObject("data")
        require(data.getJSONObject("edition").getString("identifier") == "tr.diyanet")
        val ayahs = data.getJSONArray("ayahs")
        require(ayahs.length() == count) { "Eksik meal" }
        return (0 until count).map { index ->
            val node = ayahs.getJSONObject(index)
            require(node.getInt("numberInSurah") == index + 1)
            node.getString("text").trim().also { require(it.isNotEmpty()) }
        }
    }
}
