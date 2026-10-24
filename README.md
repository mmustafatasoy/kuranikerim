# NUR — Kur'an-ı Kerim & İslam Rehberi

**Android 8.0 (API 26) ve üstünü hedefleyen Türkçe, çevrimdışı Kur'an okuyucu.**

## Yeni sürüm (v0.2.0) — internetten sesli kıraat

- Sure okuma ekranında **Sureyi oku** ile Mishary Rashid Alafasy kıraatini başlat.
- Her ayetin yanındaki **▶** düğmesiyle seçilen ayetten itibaren sesli okumaya başla.
- Oynat / duraklat / önceki ayet / sonraki ayet / ayeti tekrar et.
- Sesli okunan ayeti görsel olarak vurgula, okuma konumunu kaydet.
- Media3 + MediaSessionService: arka planda ve kilit ekranında oynatma.
- Ayet MP3'leri **Al Quran Cloud CDN** üzerinden internet bağlantısıyla çalınır. Bu sürümde otomatik çevrimdışı indirme yoktur.
- Önceden bulunan kullanıcıya ait yerel MP3 seçme özelliği duruyor (isteğe bağlı).

## Temel okuma bölümleri (v0.1.0)

- 114 sure ve 6.236 ayetin tamamı cihazın içinde; Kur'an metni için internet gerekmez.
- Tanzil **harekeli Osmanî yazım** kaynak metni, değiştirilmeden arşiv içinde saklanır.
- Sure numarası ve adıyla arama; sure listesi.
- Büyük yazılı, sağdan sola Arapça ayet kartları; yazı boyutu ayarı.
- Okunan son sure ve görünür ayeti kaydetme ve devam etme.
- İstenen ayete yer imi ekleme.
- Bir sureye ait **kullanıcının kendi cihazından seçtiği** ses dosyasını çevrimdışı oynatma.
- Elifba harf tanıtım kartları.
- Açık/koyu tema; ibadet modülleri için şeffaf yol haritası.

### Henüz yapılmayanlar

Harf veya kelimeye dokununca *doğrulanmış telaffuz sesi* bu sürümde yoktur. Türkçe meal ve tefsir, otomatik hafız ses paketi indirme, kelime-ses senkronizasyonu, ezan/namaz vakitleri, kıble, tecvid dersleri henüz uygulanmadı; arka planda Media3 oynatma v0.2.0 ile eklendi. İbadet sayfasındaki kartlar bilgi amaçlıdır, sahte içerik üretmez.

> **Önemli:** Bu sürümün ayet kartları 604 sayfalık geleneksel basılı mushaf dizilimi değildir. Gerçek sayfa dizilimi ayrı lisanslı yerleşim verisi gerektirir.

## Android Studio ile açma

1. Depoyu **Code > Download ZIP** ile indir veya Git ile klonla.
2. Android Studio'da kök klasörü aç.
3. JDK 17 ve Android SDK Platform 36'nın yüklü olduğundan emin ol.
4. Gradle senkronizasyonu tamamlandıktan sonra `app` çalıştırma yapılandırmasını seç.
5. Emülatör veya Android 8.0+ cihazda çalıştır.

Komut satırı (JDK 17 ve Android SDK gerekir):

```bash
./gradlew :app:assembleDebug
```

Oluşan debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Ses nasıl eklenir?

Bir sureyi aç, **MP3 seç** düğmesine bas ve cihazındaki uygun, yasal ses dosyasını seç. Uygulama belge URI okuma iznini saklar. Seçim o sure için korunur. Otomatik paket indirimi veya hafız senkronizasyonu yapmaz; ses oynatımı ekran açıkken çalışan temel sürümdür.

## Mimari

- Kotlin + Jetpack Compose + Material 3
- Android Gradle Plugin 8.13 / Kotlin 2.2.20 / Gradle 8.13
- Media3 ExoPlayer + MediaSessionService 1.11.1; Al Quran Cloud ayet ses akışı
- Yerel UTF-8 veri: `app/src/main/assets/`
- Sure/ayet ayrıştırıcısı: `QuranRepository.kt`
- Kalıcı okuma konumu/yer imleri: `ProgressStore.kt` (SharedPreferences, commit)
- Compose ekranları ve yerel ses: `MainActivity.kt`

İleride daha karmaşık ilerleme kayıtları için Room kullanılabilir; ses Media3 servisine taşındı.

## Dini metin ve lisans bilgisi

**Kur'an Arapça metni:** Tanzil Quran Text, © 2007–2021 Tanzil Project, **CC BY 3.0**. Kaynak: https://tanzil.net/ . Metin değiştirilmemelidir. Orijinal telif bloğu `app/src/main/assets/quran-uthmani.txt` dosyasında aynen korunur.

**Sure metadata:** Mushaf-Learning/quran-text (MIT, kaynak: Tanzil metadata); lisans ve kaynak bilgileri `THIRD_PARTY_NOTICES.md` içindedir.

**Uygulama kaynak kodu:** Bu deponun kökündeki `LICENSE` (MIT). Uygulama kodunun MIT lisansı, ayrı lisanslı Kur'an metninin şartlarını ortadan kaldırmaz.

Metinler kaynakla karşılaştırılarak doğrulanmalı; yeni meal, tefsir ve ses kayıtlarında ayrı lisans ve içerik kontrolleri yapılmalıdır.

## Veri doğrulama

```bash
python3 scripts/validate_data.py
```

Bu işlem 114 sure ve 6.236 ayet bütünlüğünü kontrol eder; ayetlerin içerik açısından uzman tasdiki yerine geçmez.

## Yol haritası

1. ✅ Çevrimdışı Kur'an / temel arayüz / kalıcı konum / yer imleri
1a. ✅ İnternet üzerinden ayet ayet hafız kıraati ve arka plan oynatma
2. ⬜ Doğrulanmış harf ve kelime sesleri, gerçek ses senkronizasyonu
3. ⬜ Lisanslı meal ve tefsir, kelime anlamları
4. ⬜ İzinli içerikler için çevrimdışı ses paket indirme (arka plan oynatma ✅)
5. ⬜ Namaz vakitleri / kıble / kaynaklı dini kütüphane
6. ⬜ Çoklu cihaz ve Android 8–16 gerçek cihaz testleri

**Durum:** İlk kaynak kod sürümü. GitHub Actions derleme sonucu kontrol edilmeden "hatasız APK" veya tüm cihazlarda sorunsuz çalışıyor garantisi verilmez.

## Çevrimiçi ses kaynağı ve haklar
- https://alquran.cloud/cdn (Al Quran Cloud / Islamic Network)
- https://alquran.cloud/terms-and-conditions
- Varsayılan kıraat: Mishary Rashid Alafasy, ar.alafasy, 64 kbps.
- CDN sözleşmesi kişisel/eğitim amaçlı dinlemeyi ve uygulamalarda akışı açıklar; ses kayıtlarının telifi okuyuculara/hak sahiplerine aittir. Paketleyip yeniden dağıtma izni otomatik verilmiş sayılmaz. Herhangi bir ses dosyası bu depoya eklenmemiştir.
- Kıraat oynatımı internet gerektirir. Bağlantı yoksa cihazdan MP3 seçilebilir.
