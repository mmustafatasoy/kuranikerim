package com.nur.quran

import org.junit.Assert.*
import org.junit.Test

class NurReleaseParserTest {
    private val validJson = """
        {
          "tag_name":"v0.8.0","draft":false,"prerelease":false,"body":"Güncelleme",
          "assets":[{
            "name":"nur-release.apk","state":"uploaded","size":40000000,
            "digest":"sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "browser_download_url":"https://github.com/mmustafatasoy/kuranikerim/releases/download/v0.8.0/nur-release.apk"
          }]
        }
        """.trimIndent()

    @Test fun parsesNewerApprovedRelease() {
        val r = NurReleaseParser.parse(validJson, "0.7.0")
        assertNotNull(r)
        assertEquals("v0.8.0", r?.tag)
    }
    @Test fun rejectsDowngradesAndUnexpectedAssetSources() {
        assertNull(NurReleaseParser.parse(validJson, "0.8.0"))
        assertNull(NurReleaseParser.parse(validJson, "1.1.0"))
        assertNull(NurReleaseParser.parse(validJson.replace("sha256:", "other:"), "0.7.0"))
        assertNull(NurReleaseParser.parse(validJson.replace(
            "https://github.com/mmustafatasoy/kuranikerim/", "https://evil.example/"
        ), "0.7.0"))
    }
    @Test fun properSemVerComparison() {
        assertTrue(NurReleaseParser.newer("v1.10.0", "1.9.9"))
        assertFalse(NurReleaseParser.newer("v1.9.9", "1.10.0"))
        assertFalse(NurReleaseParser.newer("v0.8.0-beta", "0.7.0"))
    }
}
