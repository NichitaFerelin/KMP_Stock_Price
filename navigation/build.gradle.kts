import com.ferelin.Base
import com.ferelin.Dependencies

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        minSdk = Base.minSDK
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(Dependencies.fragments)
}