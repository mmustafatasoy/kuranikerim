package com.nur.quran

import android.content.Context
import org.json.JSONArray

/**
 * A displayed Arabic grapheme consists of a base code point and its following
 * Quranic combining marks. Harakat never become independently timed "letters".
 *
 * Indices are in logical Quran reading order, NOT visual LTR screen positions.
 * This leaves Arabic joining, Amiri Quran shaping and the source text untouched.
 */
data class QuranGlyph(val text: String, val wordIndex: Int, val letterIndex: Int)

object ArabicLetterTimeline {
    private fun combining(codePoint: Int): Boolean {
        val category = Character.getType(codePoint)
        return category == Character.NON_SPACING_MARK.toInt() ||
            category == Character.COMBINING_SPACING_MARK.toInt() ||
            category == Character.ENCLOSING_MARK.toInt() ||
            codePoint == 0x0640 // tatweel, keep attached to preceding glyph
    }

    fun glyphs(text: String): List<QuranGlyph> {
        val result = mutableListOf<QuranGlyph>()
        var wordIndex = -1
        var newWord = true
        var i = 0
        while (i < text.length) {
            val cp = Character.codePointAt(text, i)
            val length = Character.charCount(cp)
            if (Character.isWhitespace(cp)) {
                newWord = true
                i += length
                continue
            }
            if (newWord) {
                wordIndex++
                newWord = false
            }
            val slice = String(Character.toChars(cp))
            if (combining(cp) && result.isNotEmpty() && result.last().wordIndex == wordIndex) {
                val prev = result.removeAt(result.lastIndex)
                result.add(prev.copy(text = prev.text + slice))
            } else {
                result.add(QuranGlyph(slice, wordIndex, result.size))
            }
            i += length
        }
        return result
    }
}

/** Genuine cue for the same reciter AND the same per-ayah audio stream. */
data class LetterCue(val glyph: Int, val startMs: Long, val endMs: Long)

object LetterCueMath {
    fun valid(cues: List<LetterCue>, count: Int): Boolean {
        if (count <= 0 || cues.size != count) return false
        return cues.withIndex().all { (i, cue) ->
            cue.glyph == i && cue.startMs >= 0 && cue.endMs > cue.startMs &&
                (i == 0 || cue.startMs >= cues[i - 1].endMs)
        }
    }

    fun fromRecordedStarts(starts: List<Long>, endMs: Long): List<LetterCue>? {
        if (starts.isEmpty() || starts.first() < 0 || endMs <= starts.last()) return null
        val ends = starts.drop(1) + endMs
        val cues = starts.indices.map { i -> LetterCue(i, starts[i], ends[i]) }
        return cues.takeIf { valid(it, starts.size) }
    }

    /** -1 means there is no aligned/recorded letter at the current position. */
    fun activeAt(cues: List<LetterCue>, ms: Long, isPlaying: Boolean): Int {
        if (!isPlaying || ms < 0) return -1
        return cues.firstOrNull { ms >= it.startMs && ms < it.endMs }?.glyph ?: -1
    }
}

/**
 * Local per-reciter, per-surah, per-ayah timing authored by the listener.
 * No timestamps are synthesized from MP3 duration, text length, or syllable count.
 * A cue produced while listening to another reciter is never applied.
 */
class LetterTimingStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "nur_manual_letter_cues_v1", Context.MODE_PRIVATE
    )
    private fun key(reciter: String, surah: Int, ayah: Int) =
        "$reciter:$surah:$ayah"

    fun load(reciter: String, surah: Int, ayah: Int, glyphCount: Int): List<LetterCue> {
        val json = prefs.getString(key(reciter, surah, ayah), null) ?: return emptyList()
        return runCatching {
            val a = JSONArray(json)
            val cues = (0 until a.length()).map { i ->
                val cue = a.getJSONArray(i)
                LetterCue(cue.getInt(0), cue.getLong(1), cue.getLong(2))
            }
            cues.takeIf { LetterCueMath.valid(it, glyphCount) } ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun save(reciter: String, surah: Int, ayah: Int, glyphCount: Int,
             cues: List<LetterCue>): Boolean {
        require(surah in 1..114 && ayah >= 1)
        require(LetterCueMath.valid(cues, glyphCount))
        val data = JSONArray()
        cues.forEach {
            data.put(JSONArray().put(it.glyph).put(it.startMs).put(it.endMs))
        }
        // commit() prevents a crash immediately after saving from losing the map.
        return prefs.edit().putString(key(reciter, surah, ayah), data.toString()).commit()
    }

    fun clear(reciter: String, surah: Int, ayah: Int) {
        prefs.edit().remove(key(reciter, surah, ayah)).commit()
    }
}
