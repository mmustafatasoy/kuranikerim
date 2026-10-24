package com.nur.quran

/** Keep the original Tanzil verses in assets. The non-numbered Basmala prefix
 * in a surah's first source line is separated for display and speech alignment.
 * Fatiha's verse 1 is the Basmala; Tawbah starts without a Basmala.
 */
object QuranTextRules {
    private val marks = Regex("[\\u0640\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
    fun normalized(text: String): String =
        text.replace(marks, "").replace('ٱ', 'ا').trim()

    fun separateOpening(number: Int, verses: List<String>): Pair<List<String>, String?> {
        require(number in 1..114 && verses.isNotEmpty())
        if (number == 1 || number == 9) return verses to null
        val words = verses[0].trim().split(Regex("\\s+"))
        if (words.size < 5) return verses to null
        val basmala = words.take(4).joinToString(" ")
        if (normalized(basmala) != "بسم الله الرحمن الرحيم") return verses to null
        val firstAyah = words.drop(4).joinToString(" ")
        require(firstAyah.isNotBlank()) { "Empty first ayah" }
        return (listOf(firstAyah) + verses.drop(1)) to basmala
    }
}
