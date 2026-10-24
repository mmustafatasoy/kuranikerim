package com.nur.quran

/** Guards against grouped (whole-surah) commentary being mislabeled as per-verse meal. */
internal object AyahMealIntegrity {
    fun check(meals: List<String>, expected: Int) {
        require(meals.size == expected && meals.none { it.isBlank() }) {
            "Meal eksik veya boş"
        }
        if (expected > 1) {
            require(meals.distinct().size > 1) {
                "Toplu meal tüm ayetlere tekrarlanmış; gösterilmeyecek"
            }
        }
    }
}
