package com.nur.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuranAudioLinksTest {
    @Test fun firstAndLastAyahHaveCorrectGlobalUrls() {
        assertEquals(
            "https://cdn.islamic.network/quran/audio/64/ar.alafasy/1.mp3",
            QuranAudioLinks.url(1)
        )
        assertEquals(
            "https://cdn.islamic.network/quran/audio/64/ar.alafasy/6236.mp3",
            QuranAudioLinks.url(6236)
        )
    }

    @Test fun outOfRangeNumberIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { QuranAudioLinks.url(0) }
        assertThrows(IllegalArgumentException::class.java) { QuranAudioLinks.url(6237) }
    }
}
