plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("org.lsposed.lsparanoid")
    id("org.lsposed.lsplugin.apktransform")
}

lsparanoid {
    global = true
    includeDependencies = true
}

android {
    namespace = "fe.antiwebview"
    compileSdk = 33

    defaultConfig {
        applicationId = "fe.antiwebview"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        prefab = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    dependenciesInfo {
        includeInApk = false
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.6.1")
    implementation("androidx.core:core:1.10.0")
    compileOnly("de.robv.android.xposed:api:82")
}