package com.nur.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuranTextRulesTest {
    @Test fun fatihaFirstAyahRemainsBasmala() {
        val first = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        val (verses, opening) = QuranTextRules.separateOpening(1, listOf(first))
        assertNull(opening)
        assertEquals(first, verses[0])
    }

    @Test fun bakaraShowsAlifLamMimSeparatelyFromBasmala() {
        val line = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ الٓمٓ"
        val (verses, opening) = QuranTextRules.separateOpening(2, listOf(line, "ذَٰلِكَ الْكِتَابُ"))
        assertEquals("بسم الله الرحمن الرحيم", QuranTextRules.normalized(opening!!))
        assertEquals("الم", QuranTextRules.normalized(verses[0]))
        assertEquals(2, verses.size)
    }

    @Test fun atTawbahNeverAddsOpening() {
        val verse = "بَرَآءَةٌ مِّنَ ٱللَّهِ"
        val (verses, opening) = QuranTextRules.separateOpening(9, listOf(verse))
        assertNull(opening)
        assertEquals(verse, verses[0])
    }

    @Test fun alreadySeparatedAyahRemainsIntact() {
        val verse = "الٓمٓ"
        val (verses, opening) = QuranTextRules.separateOpening(2, listOf(verse))
        assertNull(opening)
        assertEquals(verse, verses[0])
    }
}
