plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.shieldrj.wellnesswatchface"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shieldrj.wellnesswatchface"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // A Watch Face Format bundle must be resource-only: no application code and no
    // code dependencies. Anything added here lands in classes.dex and contradicts
    // android:hasCode="false" in the manifest.

    // Unit tests only - these do not ship in the APK.
    testImplementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13.2")
}

