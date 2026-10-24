# NUR — Kur'an-ı Kerim & İslam Rehberi

**Android 8.0 (API 26) ve üstünü hedefleyen Türkçe, çevrimdışı Kur'an okuyucu.**

## NUR v0.9.0 — HTML Premium V2'den Android ana sayfa

- Kullanıcının renkli hareketli HTML taslağı gerçek Jetpack Compose ana sayfasına aktarıldı: koyu zümrüt/turkuaz hero, altın besmele kartı, cami silüeti ve büyük harekeli Arapça **ٱللّٰهُ** / **مُحَمَّدٌ** madalyonları.
- Arapça harfler, üstün/esre/ötre ve şedde işaretleri Amiri Quran fontuyla korunur. Dönen unsur yazı değil **süsleme halkasıdır**; yazılar okunabilir ve sabit kalır.
- Dört fonksiyonel renkli kart: Kur’an (sureler), İbadet Rehberi, Elifba, Sesli Kütüphane. Sesli Kütüphane ayrı gerçek Compose ekranında hazır dört sureyi ve tüm sureleri açar.
- Günlük ayet örneği: Bakara 2:153; tıklandığında Bakara suresi açılır. Sabit örnektir, her gün sunucudan değişmez.
- Alt menü Ana Sayfa, Kur’an, İbadet, Kütüphane, Ayarlar olacak şekilde yenilendi. Arama ana sayfanın üst kısmında erişilebilir.
- Açık/koyu tema ve GitHub güncelleme denetimi yeni ekrandan kullanılabilir. Okuyucu, kıraat, İbadet Rehberi, çevrimdışı ses, Android safeDrawing ve mevcut yer imleri korunur.
- **Dağıtım kısıtı:** Bu kod main'e geçtiğinde GitHub Actions debug APK üretebilir, ancak telefonlara uygulama içi güncelleme otomatik ulaşabilmesi için `v0.9.0` imzalı GitHub Release gerekir. Kalıcı imzalama secret'ları ayarlanmadan signed yayın gerçekleşmez. Önceki debug APK ile imza uyuşmazlığı ilk kurulumda söz konusu olabilir.

## NUR v0.8.0 — Güncelleme Sistemi (GitHub Releases)

- Uygulama açılınca GitHub'ın yayımlanan son kararlı sürümü HTTPS üzerinden kontrol edilir.
- Yalnızca mmustafatasoy/kuranikerim reposundaki nur-release.apk adlı asset kabul edilir.
- Sürüm güncel APK'dan büyük olmalı; Github SHA-256 özeti, dosya boyutu, paket adı ve APK sürümü uyuşmalıdır.
- Ayarlar → Güncellemeleri kontrol et menüsü elle tekrar sorgulama sağlar.
- Kullanıcı İndir dediğinde Android DownloadManager ile arka planda APK indirilir. Kur düğmesi Android'in açık onay isteyen yükleme ekranını açar; sessiz kurulum yapılmaz.
- Releases listesi boşsa uygulama güncel/henüz yayımlanmış sürüm yok mesajı gösterir.
- Kod push etmek güncelleme yayımlamaz: ayrıca **imzalı GitHub Release** oluşturulmalıdır.

### Tek seferlik zorunlu imzalama kurulumu

Eski NUR APK'ları CI sisteminde debug imzasıyla üretilmiştir. Debug anahtarları farklı CI çalıştırmalarında değişebilir; yeni imzalı release APK bunların üzerine güncelleme olarak kurulamayabilir. İlk geçişte eski debug uygulamasını kaldırıp yeni release sürümünü kurmak gerekebilir, bu da yer imleri, kayıtlı ilerleme ve sesleri silebilir. Kaybedilmesini istemediğiniz verileri korumadan eski uygulamayı kaldırmayın.

1. Kendi güvenli bilgisayarında bir defa release keystore oluştur (terminal):  
   keytool -genkeypair -v -keystore nur-release.jks -alias nur -keyalg RSA -keysize 3072 -validity 10000
2. Keystore dosyasını ve parolaları güvenli yerde yedekle. Dosyayı herkese açık GitHub'a veya sohbete asla yükleme.
3. Keystore dosyasını Base64 kodla (Linux): base64 -w 0 nur-release.jks  
   macOS: base64 < nur-release.jks | tr -d '\n'
4. GitHub → Settings → Secrets and variables → Actions → New repository secret:
   - NUR_RELEASE_KEYSTORE_B64: Base64 keystore içeriği
   - NUR_KEYSTORE_PASSWORD: Keystore parolası
   - NUR_KEY_ALIAS: nur
   - NUR_KEY_PASSWORD: Keystore içindeki anahtarın parolası
5. Sürüm numarasını ve code değerini yükselt (ör. versionName 0.8.0 / versionCode 9).
6. Ana dala bu sürümün kodu geldikten sonra terminalden sırayla çalıştır:  
   git tag v0.8.0  
   git push origin v0.8.0
7. GitHub Actions'daki NUR Signed GitHub Release iş akışı aynı anahtarla imzalanmış APK üretip nur-release.apk ve SHA256SUMS.txt olarak GitHub Releases'a yayımlar.
8. Arkadaşlarına yalnızca GitHub Releases'dan imzalı APK ver; debug artifacts dosyalarını bir daha güncelleme dağıtımı için kullanma.

Depoda bu dört secret tanımlı değilse otomatik yayın işi güvenlik nedeniyle hata verecek ve imzasız sürüm yayınlamayacaktır. Anahtar ve secret ayarlarını sadece depo yöneticisi yapabilir.

### Google Play notu

Bu güncelleme sistemi doğrudan GitHub APK dağıtımı içindir. Google Play dağıtımı yapılacaksa mağazanın uygulama içi güncelleme altyapısı kullanılmalı, Play'in REQUEST_INSTALL_PACKAGES kurallarına uyulmalıdır. Android kullanıcı onayı olmadan sessiz APK yüklemesi yapılmaz.

## NUR v0.7.0 — Arapça hat, premium ana sayfa, İbadet Rehberi ve çevrimdışı kıraat

- Sure başlıklarının iki yanında geleneksel Amiri Quran yazı tipinde **الله** ve **محمد** yazılı süslü madalyonlar yer alır. Arabesk halka yavaşça döner, Arapça hat yazıları sabittir.
- Altın çerçeveli ve krem zeminli sure levhası, doğru besmele kuralını korur (Fâtiha 1:1 tekrar edilmez; Tevbe'de yoktur).
- Yeni ana sayfa: devam et kartı, 114 sure, Elifba, İbadet Rehberi, çevrimdışı dinle ve dört kısa sure kısayolu.
- İbadet Rehberi: abdest, iki rekât namaz, gusül, teyemmüm, namazda okunan dualar. İlk dersler ve okunuşları telefon içinde metin olarak bulunur; bilgiler Diyanet kaynaklarına dayanır ve Hanefî anlatım ağırlıklıdır.
- **Harici MP3 dosyası seçme özelliği kaldırıldı.** Kıraat için ayrı uygulamaya veya dosya yöneticisine gitmek gerekmez.
- **APK ile gelen ses paketi:** Mishary Rashid Alafasy 64 kbps, 22 ayet; Fâtiha (1:1–7), İhlâs (112:1–4), Felak (113:1–5), Nâs (114:1–6). Çevrimdışı başlangıç dinlemesi için internet gerekmez.
- Diğer surelerde oynatıcıdan **Sureyi indir** işlemi ayet seslerini uygulamanın özel dizinine kaydeder. Daha sonra bağlantı olmadan dinlenir. Hafızlar ayrı saklanır ve yeterli depolama gerekir.
- Çevrimdışı ses hakları kaynağı: https://alquran.cloud/terms-and-conditions . Ticari dağıtım planlanıyorsa hak sahipliği ve güncel şartlar ayrıca kontrol edilmelidir.
- Türkçe mealin internetten ilk yüklenme şartı değişmemiştir; daha önce alınan mealler çevrimdışı açılır. İbadet metinleri çevrimdışı hazırdır.
- İbadet rehberindeki bütün Arapça dualar için ayrı stüdyo sesleri henüz mevcut değildir. Hazır sesler belirtilen dört Kur’an suresine aittir; başka ses varmış gibi gösterilmez.
- Android 8–16 ve sistem çubuklarına uyum, mevcut ayet harf takibi ve tüm kayıtlar korunur.

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
