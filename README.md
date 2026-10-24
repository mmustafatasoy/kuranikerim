# NUR — Kur'an-ı Kerim & İslam Rehberi

**Android 8.0 (API 26) ve üstünü hedefleyen Türkçe, çevrimdışı Kur'an okuyucu.**

## Sesle harf eşleştirme v0.6.0

- Okuma ekranına **harf eşleştirme** (kalem simgesi) eklendi. Ayeti dinlerken, sesin başladığı her harfi sırayla işaretleyip tamamını cihazda kaydedebilirsin.
- Ayrıştırıcı Arapça esas harfleri, harekeleri ve diğer birleştirici işaretleri bir arada tutar; orijinal Kur’an metni değiştirilmez.
- Media3 oynatıcısının **gerçek ayet ses konumu (ms)** 70 ms aralıklarla dinlenir. Kayıtlı bir başlangıç/bitiş işaretine rastlandığında yalnızca o harf kırmızı görünür; diğer harfler orijinal renklerini korur.
- Bu zamanlama **her ayete ve hafıza özel** saklanır. Başka hafızın kaydıyla karşılaştırılıp kaydırılmaz. Kullanıcı eşleştirmesi hatalıysa geri alıp yeniden işaretleyebilir.
- **Sınırlama:** Al Quran Cloud MP3 kayıtlarında doğrulanmış harf-seviyesi zamanlama bulunmuyor. Bu sürümde **otomatik tahmin yapılmaz**, eşleştirilmemiş ayette sadece aktif ayet/meal vurgusu çalışır. Harflerin kendiliğinden doğru zamanlarda kırmızı olabilmesi için ilgili ayetin elle uzman eşleştirilmesi veya aynı ses kaydına ait lisanslı ve doğrulanmış zaman verileri gereklidir.
- Harf eşleme ekranını açmak için ayet satırındaki **kalem** simgesine dokun. Ayet oynatılır; duyduğun harflerin başlangıcını işaretle ve sonuncudan sonra kaydet.
- Kayıtlar `SharedPreferences` içinde yerel saklanır. İlgili sure/ayet hafızının kaydından başka kayda taşınmaz.
- Aynı sesli ayete tekrar başlatma, ilerletme veya oynatma hızını değiştirmede zaman konumuna göre vurgulama yapılır.
- Sesli tüm Kur’an ve mevcut v0.5.1 mealler, beyaz Amiri Quran sayfası, sistem alanları değişmeden korunmuştur.

## Hata düzeltmesi v0.5.1 — Nâs ve bütün surelerde ayet bazlı doğru Türkçe meal

- Nâs suresinde 1–6 ayetlerinin tamamında aynı uzun Diyanet mealinin görünmesinin nedeni: `tr.diyanet` kaynağı bazı ayetleri toplu çevirmektedir.
- Okuyucu meal kaynağı, **Şaban Piriş / QuranEnc `turkish_shaban`** ayet bazlı tercümesine geçirildi.
- Her ayetin farklı doğru çevirisi, doğru sure ve ayet numarasına göre ayrı ayrı getirilip gösterilir.
- Eksik, sırası karışmış veya tüm ayetlerde aynı toplu meal bulunan veri ekrana basılmaz.
- Eski `tr.diyanet-v1` önbelleği hiç kullanılmaz. Yeni meal ilk kez internet üzerinden getirilir ve sonra cihazda saklanır.
- Görsel tasarım, renkli Amiri Quran yazısı, kıraat oynatıcısı ve Android sistem çubuğu güvenli alanları korunur.
- Kaynak ve şartlar: https://quranenc.com/tr/browse/turkish_shaban , https://quranenc.com/en/home/api

## Önceki sürüm v0.5.0 — referans ekranlar ve Android sistem çubukları

- Kullanıcının verdiği referans görsel temel alınarak 114 sure listesi, beyaz mushaf ekranı, renkli büyük Amiri Quran yazıları, aktif ayet-meal vurgusu, yeşil sabit oynatıcı, alt menü ve ayet ayarları paneli gerçek Compose bileşenleri olarak kuruldu.
- Android 8–16: enableEdgeToEdge + safeDrawing güvenli boşlukları; üstte saat, SIM, şarj ve altta hareket/gezinti alanları hiçbir ana bileşeni örtmez. Tam ekran/sistem barları üzerine uzanma düzeltilmiştir.
- Sure arama, Mekke/Medine filtresi, ayet seçme, ayet yer imi, okuma boyutu ve Türkçe meal kontrolü.
- Ayar tercihlerinin kalıcı depolanması; desteklenen hafızlar: Alafasy, Sudais, Husary, Minshawi.
- Gerçek Media3 oynatma, süre çubuğu ile ileri/geri sarma, hız değiştirme, tekrar ve otomatik sonraki ayet kontrolü.
- **Henüz yok:** Harf/kelime sesi, doğru tecvid renk algoritması ve lisanslı otomatik indirme. Bunlar sahte çalışan düğmelerle temsil edilmez.
- Varsayılan çevrimiçi ses kalitesi, sağlayıcının tanımladığı hafız sürümüne uygun olarak seçilir.
- Gerçek cihazda piksel karşılaştırması, farklı ekran kesikleri ve gezinme biçimleri için ek saha testi gerekmektedir.

## Önceki sürüm v0.4.0 — gerçek Kur’an fontu ve düzeltilmiş ayet düzeni

- Referans görsele uygun beyaz okuma sayfası, zümrüt yeşili başlık, altın süslemeler, canlı renkli büyük Arapça kelimeler ve Türkçe meal.
- **Amiri Quran** hattatlık yazı tipi (Google Fonts, SIL Open Font License 1.1), Android APK derlemesinde güvenilir kaynak ve Git hash kontrolüyle gömülür; internet olmadan da Arapça font çalışır.
- Bazı Tanzil metin dışa aktarımlarında sure girişindeki **ayet dışı besmele**, 2:1 gibi ilk ayetin aynı satırında bulunur. Yeni ayrıştırıcı besmeleyi süslü başlıkta ayrı gösterir; Bakara 2:1 yalnızca Elif Lâm Mîm ayetidir.
- Fâtiha 1:1 ayeti besmeledir; iki kez gösterilmez. Tevbe başına besmele eklenmez.
- Okunan ayette Arapça ve Türkçe birlikte kırmızı, kelimeye elle dokunulduğunda seçilen kelime kırmızı. Otomatik kelime/harf zamanlaması henüz yoktur.
- Alt şeritte surelere git, görünür ayeti kaydet, yer imine geç, okumayı büyüt, meali gizle/göster ve arka plan ses kontrolü.
- **Font derlemesi ilk seferde internet gerektirir.** Amiri Quran lisansı `third_party/AmiriQuran-OFL.txt` ve APK içindeki `assets/fonts/AmiriQuran-OFL.txt` dosyasında saklanır.
- Test: `./gradlew :app:testDebugUnitTest :app:assembleDebug`.

## Önceki sürüm (v0.3.0) — Renkli Arapça / Türkçe meal
- Beyaz ve fildişi zeminde büyük, çok renkli Arapça metin.
- Kıraat sırasında **aktif ayet** ve onun Türkçe meali aynı anda kırmızı vurgulanır.
- Kullanıcı kelimeye dokunduğunda seçilen Arapça kelime kırmızı görünür.
- Not: Kelime/harf seviyesinde gerçek ses zaman damgaları henüz yok; otomatik renk takibi **ayet seviyesindedir**, eşleşme uydurulmaz.
- Türkçe meal kaynağı: Al Quran Cloud **tr.diyanet**; internetle getirilir, başarıyla açıldığında cihazda çevrimdışı okunabilir.
- Eski yer imleri, kaldığın yer ve yerel MP3 seçimi korunmuştur.

## Önceki sesli sürüm (v0.2.0) — internetten sesli kıraat

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
