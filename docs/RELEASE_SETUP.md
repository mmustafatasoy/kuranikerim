# NUR — Sürüm güncellemelerini yayınlama

## Durum

NUR v0.8.0: Uygulama başlangıcında ve Ayarlar ekranında GitHub Releases sürümü kontrol edilir. Yeni kararlı bir sürüm, yalnızca resmî repo URL'sinde nur-release.apk adıyla ve SHA-256 özetiyle yayımlanmışsa kullanıcıya teklif edilir. APK uygulama içinden indirilir ve Android'in kurulum onayı açılır; sessiz kurulum yapılmaz.

**Önemli imza sorunu:** 0.7.x paketleri GitHub Actions üzerinde assembleDebug ile derlenmişti. Debug anahtarının aynı hali korunmamışsa yeni kalıcı release sertifikasıyla imzalanan APK eskisinin üzerine kurulamaz. İlk geçişte v0.7.x'i kaldırıp v0.8.0'ı kurmak gerekebilir ve eski uygulama verileri (okuma konumu, yer imleri, önbellek) silinebilir. Sonraki sürümler hep aynı kalıcı release sertifikasıyla imzalanırsa mevcut veriler korunur.

## Bir defalık, gizli imzalama kurulumu

1. Depo sahibi kendi güvenli bilgisayarında Android yayın keystore'u üretir; örnek komut:

    keytool -genkeypair -keystore nur-release.jks -alias nur-release -keyalg RSA -keysize 3072 -validity 10000 -storetype PKCS12

2. Keystore, şifreler ve yedekleri güvenli tutulur. Repoya, issue'ya ya da açık sohbete yüklenmez. Linux'ta base64 -w 0 nur-release.jks komutu dosyanın base64 biçimini üretir; çıkan değeri kamuya açık paylaşmayın.
3. GitHub deposunda Settings → Secrets and variables → Actions bölümünde dört repository secret eklenir:
    - NUR_RELEASE_KEYSTORE_B64: keystore base64 içeriği
    - NUR_KEYSTORE_PASSWORD: keystore parolası
    - NUR_KEY_ALIAS: ör. nur-release
    - NUR_KEY_PASSWORD: özel anahtar parolası
4. Keystore kaybedilirse bu sertifikayla imzalanmış APK'ların üzerine normal güncelleme yayımlama imkânı kaybolabilir. Anahtarlar tüm gelecek sürümlerde aynen korunmalıdır.

## Yayın iş akışı

1. Main dalında app/build.gradle.kts içindeki versionCode her sürümde artırılır ve versionName yeni sürümle eşleşir.
2. Main dalındaki doğrulanmış commit'e tam vX.Y.Z biçimli etiket (ör. v0.8.0) gönderilir.
3. .github/workflows/release.yml bu etiketi görüp imzalama secret'larını doğrular, derler, test eder, apksigner ile imzayı kontrol eder ve APK'daki 22 ses dosyasını sınar.
4. İmzalı APK, SHA256SUMS.txt dosyasıyla beraber GitHub Releases sayfasına nur-release.apk adıyla yayımlanır.
5. Yeni Android cihazına kullanıcı bu resmî APK'yı kurar. Bundan sonraki sürümlerde NUR, yeni sürümü kendisi bulup kullanıcının İndir ve Kur adımlarıyla güncellenebilir.

GitHub Actions nur-debug-apk artifact'ı resmî GitHub Release değildir. İlk release yayımlanmadan uygulama bir güncelleme teklif edemez. Keystore secret'ları kurulmamışsa release işi bilinçli olarak başarısız olur; herhangi bir debug APK yanlışlıkla release olarak yayımlanmaz.

## Sınırlamalar

- Android 8+ kullanıcı onaylı APK yükleyicisine yönlendirilir. Android için yükleme izni cihaz politikalarına göre gerekebilir.
- İçerik güncellemesi APK güncellemesinden ayrıdır; bu altyapı uygulama sürümü içindir.
- Play Store'dan dağıtılan uygulamalarda Play'in kendi güncelleme sistemi tercih edilmelidir.
- Gerçek cihazda eski debug sürümden geçiş, imza uyumu, düşük depolama ve indirme kesintileri mutlaka ayrıca denenmelidir.
