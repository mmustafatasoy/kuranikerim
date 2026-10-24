import java.security.MessageDigest
import java.net.URI

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.nur.quran"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.nur.quran"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "0.6.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.06.01")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.media3:media3-exoplayer:1.11.1")
    implementation("androidx.media3:media3-session:1.11.1")
    testImplementation("junit:junit:4.13.2")
}


// Amiri Quran • SIL Open Font License 1.1.
// Fetch pinned font on the build host, verify Git blob SHA-1 and package into APK.
// Source: https://github.com/google/fonts/tree/main/ofl/amiriquran
val prepareAmiriQuran by tasks.registering {
    val file = layout.projectDirectory.file("src/main/res/font/amiri_quran.ttf").asFile
    outputs.file(file)
    doLast {
        fun blobHash(data: ByteArray): String {
            val md = MessageDigest.getInstance("SHA-1")
            md.update(("blob " + data.size + "\u0000").toByteArray(Charsets.UTF_8))
            md.update(data)
            return md.digest().joinToString("") { "%02x".format(it) }
        }
        val expected = "2a4de2c4fd3e6fd23656586151935a98723acaff"
        if (!file.isFile || blobHash(file.readBytes()) != expected) {
            val source = "https://raw.githubusercontent.com/google/fonts/e7db6ca603fd4862010383ff17ffd6cc4ffac1c0/ofl/amiriquran/AmiriQuran-Regular.ttf"
            val connection = URI(source).toURL().openConnection()
            connection.connectTimeout = 20000
            connection.readTimeout = 30000
            val content = connection.getInputStream().use { it.readBytes() }
            check(blobHash(content) == expected) { "Unexpected Amiri Quran font version" }
            file.parentFile.mkdirs()
            file.writeBytes(content)
        }
        check(blobHash(file.readBytes()) == expected)
    }
}
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(prepareAmiriQuran)
}
