package com.nur.quran

import android.content.Context

data class ReadingPosition(val surah: Int, val ayah: Int)

class ProgressStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("nur_reader_1", Context.MODE_PRIVATE)

    fun position(): ReadingPosition = ReadingPosition(
        surah = prefs.getInt("last_surah", 1).coerceIn(1, 114),
        ayah = prefs.getInt("last_ayah", 1).coerceAtLeast(1)
    )

    // commit() makes a completed save durable before the call returns.
    fun savePosition(surah: Int, ayah: Int): Boolean {
        if (surah !in 1..114 || ayah < 1) return false
        return prefs.edit()
            .putInt("last_surah", surah)
            .putInt("last_ayah", ayah)
            .commit()
    }

    fun bookmarks(): Set<String> = prefs.getStringSet("bookmarks", emptySet())?.toSet() ?: emptySet()

    fun hasBookmark(surah: Int, ayah: Int) = bookmarks().contains("$surah:$ayah")

    fun toggleBookmark(surah: Int, ayah: Int): Boolean {
        val key = "$surah:$ayah"
        val values = bookmarks().toMutableSet()
        if (!values.add(key)) values.remove(key)
        return prefs.edit().putStringSet("bookmarks", values).commit()
    }

    fun fontSize(): Int = prefs.getInt("font_size", 32).coerceIn(24, 52)

    fun setFontSize(size: Int) {
        prefs.edit().putInt("font_size", size.coerceIn(24, 52)).commit()
    }

    fun isDark(): Boolean = prefs.getBoolean("dark_theme", true)

    fun setDark(value: Boolean) {
        prefs.edit().putBoolean("dark_theme", value).commit()
    }

    fun audioUri(surah: Int): String? = prefs.getString("local_audio_$surah", null)

    fun setAudioUri(surah: Int, uri: String) {
        prefs.edit().putString("local_audio_$surah", uri).commit()
    }
}
