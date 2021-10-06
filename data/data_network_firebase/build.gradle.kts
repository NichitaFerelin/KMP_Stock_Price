import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Projects

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    compileSdk = Base.currentSDK

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(Projects.domain))
    implementation(project(Projects.shared))
    implementation(Dependencies.timber)

    implementation(Dependencies.kotlinLib)
    implementation(Dependencies.kotlinCoroutines)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)

    implementation(platform(Dependencies.firebasePlatform))
    implementation(Dependencies.firebaseAnalyticsKtx)
    implementation(Dependencies.firebaseDatabaseKtx)
}