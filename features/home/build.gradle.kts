import com.ferelin.Libs

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Libs.Project.currentSDK

    defaultConfig {
        minSdk = Libs.Project.minSDK
    }
    buildFeatures.apply {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.Compose.version
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(":core:ui"))
}