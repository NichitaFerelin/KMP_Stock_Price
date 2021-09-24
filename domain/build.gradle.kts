import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Plugins

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Base.currentSDK

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(Dependencies.kotlinLib)
    implementation(Dependencies.kotlinCoroutines)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)
}