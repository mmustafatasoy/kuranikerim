package com.nur.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AyahMealIntegrityTest {
    @Test fun nasMustHaveSixDifferentVerseTranslations() {
        val nas = listOf(
            "De ki: İnsanların Rabbine sığınırım.",
            "İnsanların melikine,",
            "İnsanların ilahına,",
            "Sinsi vesvesecinin şerrinden,",
            "İnsanların gönüllerine vesvese sokan,",
            "Cinlerden ve insanlardan!"
        )
        AyahMealIntegrity.check(nas, 6)
        assertEquals("İnsanların ilahına,", nas[2])
    }
    @Test fun rejectsRepeatedWholeSurahTranslation() {
        val repeated = List(6) { "Nâs suresinin tümünü kapsayan tek birleşik meal." }
        assertThrows(IllegalArgumentException::class.java) {
            AyahMealIntegrity.check(repeated, 6)
        }
    }
    @Test fun rejectsWrongAyahCountAndBlankTexts() {
        assertThrows(IllegalArgumentException::class.java) {
            AyahMealIntegrity.check(listOf("A", "B"), 6)
        }
        assertThrows(IllegalArgumentException::class.java) {
            AyahMealIntegrity.check(listOf("A", ""), 2)
        }
    }
}
