package com.nur.quran

import android.app.DownloadManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

internal data class NurRelease(
    val tag: String,
    val url: String,
    val sha256: String,
    val bytes: Long,
    val notes: String
)
internal enum class UpdatePhase {
    IDLE, CHECKING, READY, DOWNLOADING, VERIFYING, DOWNLOADED,
    NO_UPDATE, ERROR, PERMISSION_REQUIRED
}

/** Only accept assets from our GitHub repo with a verified SHA-256 digest. */
internal object NurReleaseParser {
    const val API = "https://api.github.com/repos/mmustafatasoy/kuranikerim/releases/latest"
    private val tagPattern = Regex("^v[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}$")
    private val hashPattern = Regex("^[a-f0-9]{64}$")

    fun versionParts(v: String): List<Int>? {
        val s = v.removePrefix("v")
        if (!s.matches(Regex("[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}"))) return null
        return s.split(".").map { it.toInt() }
    }
    fun newer(remoteTag: String, installed: String): Boolean {
        val a = versionParts(remoteTag) ?: return false
        val b = versionParts(installed) ?: return false
        for (i in 0..2) {
            if (a[i] > b[i]) return true
            if (a[i] < b[i]) return false
        }
        return false
    }
    fun parse(json: String, installed: String): NurRelease? {
        val root = JSONObject(json)
        if (root.optBoolean("draft") || root.optBoolean("prerelease")) return null
        val tag = root.optString("tag_name")
        if (!tagPattern.matches(tag) || !newer(tag, installed)) return null
        val items = root.optJSONArray("assets") ?: return null
        for (i in 0 until items.length()) {
            val a = items.optJSONObject(i) ?: continue
            if (a.optString("name") != "nur-release.apk" ||
                a.optString("state") != "uploaded") continue
            val url = a.optString("browser_download_url")
            val approved = "https://github.com/mmustafatasoy/kuranikerim/releases/download/" +
                tag + "/nur-release.apk"
            if (url != approved) continue
            val sha = a.optString("digest").removePrefix("sha256:").lowercase()
            if (!hashPattern.matches(sha)) continue
            val bytes = a.optLong("size")
            if (bytes !in 1025L..1_900_000_000L) continue
            return NurRelease(tag, url, sha, bytes, root.optString("body").take(1500))
        }
        return null
    }
}

/**
 * Self-hosted update flow: API check -> user downloads -> SHA256/APK validation
 * -> Android installer screen with explicit approval. Never installs silently.
 */
internal class NurUpdateManager(context: Context) {
    private val app = context.applicationContext
    private val downloads = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val prefs = app.getSharedPreferences("nur_update_1", Context.MODE_PRIVATE)

    var phase by mutableStateOf(UpdatePhase.IDLE)
        private set
    var release by mutableStateOf<NurRelease?>(null)
        private set
    var message by mutableStateOf("")
        private set
    private var verifiedFile by mutableStateOf<File?>(null)

    private fun installedVersion(): String {
        @Suppress("DEPRECATION")
        return app.packageManager.getPackageInfo(app.packageName, 0).versionName ?: "0.0.0"
    }

    suspend fun check(manual: Boolean = false) {
        if (phase == UpdatePhase.DOWNLOADING || phase == UpdatePhase.VERIFYING) return
        phase = UpdatePhase.CHECKING
        message = "Yeni NUR sürümleri kontrol ediliyor…"
        try {
            val payload = withContext(Dispatchers.IO) {
                val connection = URL(NurReleaseParser.API).openConnection() as HttpURLConnection
                connection.connectTimeout = 9000
                connection.readTimeout = 13000
                connection.setRequestProperty("Accept", "application/vnd.github+json")
                connection.setRequestProperty("User-Agent", "NurQuran-UpdateClient")
                try {
                    when (connection.responseCode) {
                        200 -> connection.inputStream.bufferedReader().use { it.readText() }
                        404 -> null
                        else -> error("Güncelleme sunucusu: " + connection.responseCode)
                    }
                } finally { connection.disconnect() }
            }
            release = payload?.let { NurReleaseParser.parse(it, installedVersion()) }
            phase = if (release != null) UpdatePhase.READY else UpdatePhase.NO_UPDATE
            message = when {
                release != null -> release!!.tag + " sürümü hazır."
                payload == null -> "Henüz yayımlanmış kararlı sürüm bulunmuyor."
                else -> "Uygulaman güncel."
            }
        } catch (_: Exception) {
            phase = UpdatePhase.ERROR
            message = "Güncellemeler kontrol edilemedi; internet bağlantını kontrol et."
        }
    }

    fun beginDownload(): Boolean {
        val version = release ?: return false
        if (phase == UpdatePhase.DOWNLOADING || phase == UpdatePhase.VERIFYING) return false
        return try {
            val folder = File(app.getExternalFilesDir(null), "updates")
            check(folder.isDirectory || folder.mkdirs())
            val target = File(folder, "nur-" + version.tag.removePrefix("v") + ".apk")
            if (target.exists()) target.delete()
            val req = DownloadManager.Request(Uri.parse(version.url))
                .setTitle("NUR " + version.tag)
                .setDescription("Güncelleme APK'sı indiriliyor")
                .setMimeType("application/vnd.android.package-archive")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(target))
            val id = downloads.enqueue(req)
            prefs.edit().putLong("id", id).putString("file", target.name)
                .putString("tag", version.tag).putString("hash", version.sha256)
                .putLong("size", version.bytes).apply()
            verifiedFile = null
            phase = UpdatePhase.DOWNLOADING
            message = "APK indiriliyor…"
            true
        } catch (_: Exception) {
            phase = UpdatePhase.ERROR
            message = "İndirme başlatılamadı. Telefon depolamasını ve bağlantıyı kontrol et."
            false
        }
    }

    suspend fun pollDownload() {
        val id = prefs.getLong("id", -1L)
        if (id <= 0L || phase == UpdatePhase.VERIFYING ||
            phase == UpdatePhase.DOWNLOADED || phase == UpdatePhase.PERMISSION_REQUIRED) return
        val status = withContext(Dispatchers.IO) {
            runCatching {
                downloads.query(DownloadManager.Query().setFilterById(id)).use { c ->
                    if (!c.moveToFirst()) return@use -1
                    val col = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (col < 0) -1 else c.getInt(col)
                }
            }.getOrDefault(-1)
        }
        when (status) {
            DownloadManager.STATUS_PENDING, DownloadManager.STATUS_PAUSED,
            DownloadManager.STATUS_RUNNING -> {
                phase = UpdatePhase.DOWNLOADING
                message = "Güncelleme indiriliyor…"
            }
            DownloadManager.STATUS_SUCCESSFUL -> verify()
            DownloadManager.STATUS_FAILED, -1 -> {
                clearPending()
                phase = UpdatePhase.ERROR
                message = "İndirme tamamlanamadı; yeniden deneyebilirsin."
            }
        }
    }

    private fun pendingFile(): File? {
        val name = prefs.getString("file", null) ?: return null
        if (!name.matches(Regex("^nur-[0-9]+\\.[0-9]+\\.[0-9]+\\.apk$"))) return null
        return File(File(app.getExternalFilesDir(null), "updates"), name)
    }

    private suspend fun verify() {
        phase = UpdatePhase.VERIFYING
        message = "İndirilen APK ve güvenlik özeti doğrulanıyor…"
        val target = pendingFile()
        val hash = prefs.getString("hash", "") ?: ""
        val size = prefs.getLong("size", -1L)
        val tag = prefs.getString("tag", "")
        val ok = withContext(Dispatchers.IO) {
            runCatching {
                require(target != null && target.isFile && target.length() == size && size > 1024)
                require(hash.matches(Regex("[a-f0-9]{64}")))
                val digest = MessageDigest.getInstance("SHA-256")
                target.inputStream().buffered().use { input ->
                    val chunk = ByteArray(128 * 1024)
                    while (true) {
                        val n = input.read(chunk)
                        if (n < 0) break
                        digest.update(chunk, 0, n)
                    }
                }
                require(digest.digest().joinToString("") { "%02x".format(it) } == hash)
                @Suppress("DEPRECATION")
                val archive = app.packageManager.getPackageArchiveInfo(target.absolutePath, 0)
                    ?: error("Geçersiz APK")
                @Suppress("DEPRECATION")
                val installed = app.packageManager.getPackageInfo(app.packageName, 0)
                require(archive.packageName == app.packageName)
                require(archive.versionName == tag?.removePrefix("v"))
                require(PackageInfoCompat.getLongVersionCode(archive) >
                    PackageInfoCompat.getLongVersionCode(installed))
                true
            }.getOrDefault(false)
        }
        if (ok) {
            verifiedFile = target
            phase = UpdatePhase.DOWNLOADED
            message = "Güncelleme doğrulandı. Kurulum için Android onayı gerekiyor."
        } else {
            target?.delete()
            clearPending()
            phase = UpdatePhase.ERROR
            message = "APK doğrulanamadı. İndirilen dosya güvenlik için silindi."
        }
    }

    fun requestInstall(): Boolean {
        val target = verifiedFile ?: return false
        if (!target.isFile || phase !in listOf(
                UpdatePhase.DOWNLOADED, UpdatePhase.PERMISSION_REQUIRED
            )) return false
        return try {
            if (Build.VERSION.SDK_INT >= 26 && !app.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:" + app.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                app.startActivity(intent)
                phase = UpdatePhase.PERMISSION_REQUIRED
                message = "NUR için yükleme iznini aç, geri dön ve Kur'a bas."
                return true
            }
            val uri = FileProvider.getUriForFile(
                app, app.packageName + ".updates", target
            )
            val install = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                clipData = ClipData.newRawUri("NUR", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(install)
            true
        } catch (_: Exception) {
            phase = UpdatePhase.ERROR
            message = "Android yükleme ekranı açılamadı."
            false
        }
    }

    private fun clearPending() {
        prefs.edit().remove("id").remove("file").remove("tag")
            .remove("hash").remove("size").apply()
        verifiedFile = null
    }
}
