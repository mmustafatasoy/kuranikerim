# Üçüncü taraf veri ve bileşen bildirimleri

## Tanzil Quran Text (Uthmani)
- Telif: Copyright (C) 2007–2021 Tanzil Project
- Lisans: Creative Commons Attribution 3.0
- Kaynak: https://tanzil.net/
- Lisans koşulları: https://tanzil.net/docs/Text_License
- Uygulama içindeki dosya: `app/src/main/assets/quran-uthmani.txt`
- Dosya https://github.com/gptforjoomla/Tanzil/blob/master/Packages/com_tanzil/site/resources/quran/quran-uthmani.txt adresindeki Tanzil metni ile aynı Git blob SHA'sına sahiptir (87cdfd0096fa3057dbf7904f925608cd2e1d100e).
- Metin değiştirilmeden dağıtılmalıdır. Orijinal Tanzil telif bloğu veri dosyasının sonundadır. Uygulama içinde kaynak bağlantısı mevcuttur.

## Quran structural metadata
- Metadata `app/src/main/assets/surahs.json`
- Kaynak: https://github.com/Mushaf-Learning/quran-text
- Kaynak depoda MIT lisanslı yapısal sure bilgileri. Tanzil kaynak atfını koruyun.
- Sure adları Türkçe gösterimi ayrı bir uygulama kod tablosudur, Arapça Kur'an metnini değiştirmez.

## Build tools
- Gradle Wrapper scripts/JAR: Gradle 8.13 (https://github.com/gradle/gradle)
- Wrapper JAR v8.13.0 upstream SHA 9bbc975c742b298b441bfb90dbc124400a3751b9
- Jetpack Compose, Kotlin, AndroidX bağımlılıkları kendi lisanslarına tabidir.
