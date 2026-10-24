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


    fun mealFontSize(): Int = prefs.getInt("meal_font_size", 16).coerceIn(12, 25)
    fun setMealFontSize(value: Int) {
        prefs.edit().putInt("meal_font_size", value.coerceIn(12, 25)).commit()
    }
    fun coloredArabic(): Boolean = prefs.getBoolean("colored_arabic", true)
    fun setColoredArabic(value: Boolean) {
        prefs.edit().putBoolean("colored_arabic", value).commit()
    }
    fun followMeal(): Boolean = prefs.getBoolean("follow_meal", true)
    fun setFollowMeal(value: Boolean) {
        prefs.edit().putBoolean("follow_meal", value).commit()
    }
    fun showMeal(): Boolean = prefs.getBoolean("show_meal", true)
    fun setShowMeal(value: Boolean) {
        prefs.edit().putBoolean("show_meal", value).commit()
    }
    fun autoNextAyah(): Boolean = prefs.getBoolean("auto_next_ayah", true)
    fun setAutoNextAyah(value: Boolean) {
        prefs.edit().putBoolean("auto_next_ayah", value).commit()
    }
    fun reciterId(): String {
        val value = prefs.getString("reciter_id", "ar.alafasy") ?: "ar.alafasy"
        return if (value in setOf("ar.alafasy", "ar.sudais", "ar.husary", "ar.minshawi"))
            value else "ar.alafasy"
    }
    fun setReciterId(value: String) {
        if(value in setOf("ar.alafasy","ar.sudais","ar.husary","ar.minshawi"))
            prefs.edit().putString("reciter_id", value).commit()
    }
    fun speed(): Float = prefs.getFloat("audio_speed", 1f).coerceIn(.75f, 1.5f)
    fun setSpeed(value: Float) {
        prefs.edit().putFloat("audio_speed", value.coerceIn(.75f, 1.5f)).commit()
    }

    fun audioUri(surah: Int): String? = prefs.getString("local_audio_$surah", null)

    fun setAudioUri(surah: Int, uri: String) {
        prefs.edit().putString("local_audio_$surah", uri).commit()
    }
}
