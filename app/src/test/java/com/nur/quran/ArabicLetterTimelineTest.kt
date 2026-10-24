package com.nur.quran

import org.junit.Assert.*
import org.junit.Test

class ArabicLetterTimelineTest {
    @Test fun combinesMarksWithTheirArabicLetter() {
        val glyphs = ArabicLetterTimeline.glyphs("إِلَٰهِ النَّاسِ")
        assertEquals(listOf("إِ", "لَٰ", "هِ", "ا", "ل", "نَّ", "ا", "سِ"), glyphs.map { it.text })
        assertEquals(listOf(0,0,0,1,1,1,1,1), glyphs.map { it.wordIndex })
    }

    @Test fun handlesBasmalaAndQuranicMarksWithoutErasingThem() {
        val input = "بِسْمِ ٱللَّهِ"
        val letters = ArabicLetterTimeline.glyphs(input)
        assertEquals(input.replace(" ", ""), letters.joinToString("") { it.text })
        assertEquals(2, letters.map { it.wordIndex }.distinct().size)
    }

    @Test fun capturedCueUsesRecordedBoundariesAndPauses() {
        val result = LetterCueMath.fromRecordedStarts(listOf(120L, 310L, 590L), 900L)!!
        assertEquals(1, LetterCueMath.activeAt(result, 315L, true))
        assertEquals(-1, LetterCueMath.activeAt(result, 315L, false))
        assertEquals(-1, LetterCueMath.activeAt(result, 901L, true))
    }

    @Test fun rejectsOutOfOrderOrFakeCues() {
        assertNull(LetterCueMath.fromRecordedStarts(listOf(300L, 200L), 500L))
        assertNull(LetterCueMath.fromRecordedStarts(listOf(120L, 310L), 310L))
        assertFalse(LetterCueMath.valid(listOf(LetterCue(0, 0, 10)), 2))
    }
}
