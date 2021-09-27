import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Projects
import com.ferelin.Versions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    defaultConfig {
        applicationId = Base.name
        minSdk = Base.minSDK
        targetSdk = Base.currentSDK
        versionCode = Base.versionCode
        versionName = Base.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures.apply {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(Dependencies.androidCore)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)

    implementation(project(Projects.domain))
    implementation(project(Projects.core))
    implementation(project(Projects.auth))
    implementation(project(Projects.shared))

    implementation(project(Projects.dataLocal))
    implementation(project(Projects.dataFirebase))
    implementation(project(Projects.dataNetworkApi))

    implementation(project(Projects.featureChart))
    implementation(project(Projects.featureForecasts))
    implementation(project(Projects.featureIdeas))
    implementation(project(Projects.featureLoading))
    implementation(project(Projects.featureLogin))
    implementation(project(Projects.featureNews))
    implementation(project(Projects.featureSearch))
    implementation(project(Projects.featureProfile))
    implementation(project(Projects.featureWelcome))

    implementation(project(Projects.featureSectionAbout))
    implementation(project(Projects.featureSectionStocks))

    implementation(project(Projects.featureStocksDefault))
    implementation(project(Projects.featureStocksFavourite))
}