import com.ferelin.Base
import com.ferelin.Projects

plugins {
    id("com.android.library")
    id("kotlin-android")
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
    implementation(project(Projects.core))

    implementation(project(Projects.featureProfile))
    implementation(project(Projects.featureChart))
    implementation(project(Projects.featureNews))
    implementation(project(Projects.featureForecasts))
    implementation(project(Projects.featureIdeas))

    implementation(com.ferelin.Dependencies.dagger)
    kapt(com.ferelin.Dependencies.daggerCompilerKapt)
}