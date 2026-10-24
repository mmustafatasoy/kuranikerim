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

## Al Quran Cloud / Islamic Network ayet kıraat akışı
- Kaynak: https://alquran.cloud/cdn
- Kullanım şartları: https://alquran.cloud/terms-and-conditions
- Seslendiren: Mishary Rashid Alafasy (`ar.alafasy`, 64 kbps).
- Ayet sesleri uzaktan akış olarak dinlenir. Ses kayıtlarının telif hakları kendi hak sahiplerine aittir.
- Kaynak dosyalar uygulama paketine veya bu depoya dahil edilmemiştir.

## Ayet bazlı Türkçe meal — Şaban Piriş (QuranEnc)
- Eski kaynak `tr.diyanet` bazı mealleri ayet gruplarına göre verir (ör. Nâs 1–6 tek meal); bunları her ayette tekrar etme hatası v0.5.1 ile düzeltildi.
- Yeni çeviri: **Şaban Piriş Türkçe tercümesi**, kaynağı ve yayıncısı **QuranEnc.com**.
- Kaynak: https://quranenc.com/tr/browse/turkish_shaban
- API: https://quranenc.com/api/v1/translation/sura/turkish_shaban/114
- Kullanım şartları: https://quranenc.com/en/home/api
- Güncel sürüm/versiyon bilgisi: https://quranenc.com/api/v1/translations/list/tr (source maintains versions).
- Metin ayet bazında kaynakta verildiği biçimiyle gösterilir; metne yorum eklenmez, ayet sırası ve sure numaraları ayrıca doğrulanır.
- Kaynak verisi depoya eklenmez. Kullanıcı okurken API'den alınır, bireysel cihaz önbelleğinde tutulur; eski hatalı önbellek kesinlikle kullanılmaz.
- QuranEnc lisansı: İçeriği değiştirmeme, kaynak ve yayıncıyı belirtme, güncel sürümü izleme ve uygunsuz reklam göstermeme koşulları vardır.
- Otomatik kıraat takibi sadece ayet düzeyindedir, kelime/harf düzeyinde ses zamanlaması hazır değildir.

## Amiri Quran (Google Fonts)
- Family: Amiri Quran, traditional Qur'anic Naskh typeface.
- Source: https://github.com/google/fonts/tree/main/ofl/amiriquran
- Author: Amiri Quran Project Authors, © 2010–2022.
- License: SIL Open Font License 1.1.
- Full license: third_party/AmiriQuran-OFL.txt and app/src/main/assets/fonts/AmiriQuran-OFL.txt.
- Binary font fetched at build from pinned Google Fonts commit and checked against Git blob SHA 2a4de2c4fd3e6fd23656586151935a98723acaff.

## Harf senkronizasyonu (v0.6.0)
- NUR harf zamanları herhangi bir dış kaynaktan üretilmez; yalnızca uygulamada **dinleyici tarafından bir ses kaydına özel** manuel işaretlenip yerelde saklanır.
- Mevcut Al Quran Cloud ayet MP3 akışları ve sahiplik şartları değişmemiştir.
- Quran Foundation kelime segmentleri, belirli chapter audio kayıtlarına aittir ve farklı bir MP3 kaydının harf konumları gibi kullanılamaz. https://api-docs.quran.foundation/docs/content_apis_versioned/4.0.0/chapter-reciter-audio-file/
