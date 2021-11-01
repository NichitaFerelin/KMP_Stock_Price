import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Projects

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        minSdk = Base.minSDK
    }
    buildFeatures.apply {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(Projects.shared))
    api(project(Projects.domain))
    api(project(Projects.navigation))

    api(Dependencies.androidLifecycle)
    api(Dependencies.timber)
    api(Dependencies.appCompat)
    api(Dependencies.browser)
    api(Dependencies.fragments)
    api(Dependencies.viewPager)
    api(Dependencies.constraintLayout)
    api(Dependencies.material)

    implementation(Dependencies.glide)
    kapt(Dependencies.glideCompilerKapt)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)

    testImplementation(Dependencies.testJunitKtx)
}