@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

val buildTime = System.currentTimeMillis()
val localProperties = Properties()
if (rootProject.file("local.properties").canRead()) {
    localProperties.load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "de.yisrime.lyricsuite"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.yisrime.lyricsuite"
        minSdk = 30
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0"
        // dexkit 在宿主进程内加载：网易云等宿主以 armeabi-v7a 运行，缺 v7a 会 UnsatisfiedLinkError
        ndk.abiFilters += setOf("arm64-v8a", "armeabi-v7a")
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "API_VERSION", "6")
        buildConfigField("int", "CONFIG_VERSION", "1")
    }

    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "LyricSuite-$versionName-$versionCode-$name-$buildTime.apk"
        }
    }

    aaptOptions.cruncherEnabled = false
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    dependenciesInfo.includeInApk = false
    kotlin.jvmToolchain(21)

    packaging {
        resources {
            excludes += "**"
            merges += "META-INF/xposed/*"
        }
        dex {
            useLegacyPackaging = true
        }
    }
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
}

dependencies {
    compileOnly(libs.libxposed.api)
    compileOnly(libs.dev.rikka.hidden.stub)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.kotlinx.serialization.json)


    implementation(libs.ezxhelper.core)
    implementation(libs.ezxhelper.xposed)
    implementation(libs.dexkit)
    implementation(libs.dev.rikka.hidden.compat)
    implementation(libs.libxposed.service)
    implementation(files("libs/Lyric-Getter-Api-7.0.0.aar"))
    implementation(libs.xkt)

}
