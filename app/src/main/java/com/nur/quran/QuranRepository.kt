package com.nur.quran

import android.content.res.AssetManager
import org.json.JSONArray
import java.nio.charset.StandardCharsets

data class Surah(
    val number: Int,
    val arabicName: String,
    val latinName: String,
    val turkishName: String,
    val revelation: String,
    val verses: List<String>
) {
    val ayahCount: Int get() = verses.size
}

class QuranRepository private constructor(val surahs: List<Surah>) {
    fun surah(number: Int): Surah = surahs.first { it.number == number }

    companion object {
        private val turkishNames = listOf(
            "Fâtiha", "Bakara", "Âl-i İmrân", "Nisâ", "Mâide", "En'âm", "A'râf",
            "Enfâl", "Tevbe", "Yûnus", "Hûd", "Yûsuf", "Ra'd", "İbrâhîm", "Hicr",
            "Nahl", "İsrâ", "Kehf", "Meryem", "Tâhâ", "Enbiyâ", "Hac", "Mü'minûn",
            "Nûr", "Furkân", "Şuarâ", "Neml", "Kasas", "Ankebût", "Rûm", "Lokmân",
            "Secde", "Ahzâb", "Sebe'", "Fâtır", "Yâsîn", "Sâffât", "Sâd", "Zümer",
            "Mü'min", "Fussilet", "Şûrâ", "Zuhruf", "Duhân", "Câsiye", "Ahkâf",
            "Muhammed", "Fetih", "Hucurât", "Kâf", "Zâriyât", "Tûr", "Necm",
            "Kamer", "Rahmân", "Vâkıa", "Hadîd", "Mücâdele", "Haşr", "Mümtehine",
            "Saf", "Cum'a", "Münâfikûn", "Tegâbün", "Talâk", "Tahrîm", "Mülk",
            "Kalem", "Hâkka", "Meâric", "Nûh", "Cin", "Müzzemmil", "Müddessir",
            "Kıyâmet", "İnsân", "Mürselât", "Nebe'", "Nâziât", "Abese", "Tekvîr",
            "İnfitâr", "Mutaffifîn", "İnşikâk", "Bürûc", "Târık", "A'lâ",
            "Gâşiye", "Fecr", "Beled", "Şems", "Leyl", "Duhâ", "İnşirâh",
            "Tîn", "Alak", "Kadir", "Beyyine", "Zilzâl", "Âdiyât", "Kâria",
            "Tekâsür", "Asr", "Hümeze", "Fîl", "Kureyş", "Mâûn", "Kevser",
            "Kâfirûn", "Nasr", "Tebbet", "İhlâs", "Felak", "Nâs"
        )

        fun load(assets: AssetManager): QuranRepository {
            val verses = assets.open("quran-uthmani.txt").bufferedReader(StandardCharsets.UTF_8).use { reader ->
                reader.lineSequence().filter { it.isNotBlank() && !it.startsWith("#") }.toList()
            }
            val raw = assets.open("surahs.json").bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            val metadata = JSONArray(raw)
            require(verses.size == 6236 && metadata.length() == 114 && turkishNames.size == 114) {
                "Kur'an veri bütünlüğü doğrulanamadı."
            }
            var cursor = 0
            val surahs = (0 until 114).map { index ->
                val item = metadata.getJSONObject(index)
                val count = item.getInt("ayah_count")
                val number = item.getInt("number")
                require(number == index + 1 && count > 0 && cursor + count <= verses.size)
                val list = verses.subList(cursor, cursor + count).toList()
                cursor += count
                Surah(
                    number = number,
                    arabicName = item.getString("name_arabic"),
                    latinName = item.getString("name_transliteration"),
                    turkishName = turkishNames[index],
                    revelation = item.optString("revelation_type"),
                    verses = list
                )
            }
            require(cursor == 6236)
            return QuranRepository(surahs)
        }
    }
}
