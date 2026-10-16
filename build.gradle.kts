plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.upipulse.tracker"
    compileSdk = 35 // Android 15 / 16 preview compatible

    defaultConfig {
        applicationId = "com.upipulse.tracker"
        minSdk = 34 // Specifically targeting Android 14 (API 34) to latest (API 35/36)
        targetSdk = 35
        versionCode = 2
        versionName = "2.0-Android14Plus"
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.11.0")
}
