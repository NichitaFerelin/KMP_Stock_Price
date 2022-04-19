import com.ferelin.Libs

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = Libs.Project.currentSDK

    defaultConfig {
        applicationId = "com.ferelin.stockprice"
        minSdk = Libs.Project.minSDK
        targetSdk = Libs.Project.currentSDK
        versionCode = Libs.Project.codeVersion
        versionName = Libs.Project.codeVersionName
    }

    buildFeatures.apply {
        compose = true
        buildConfig = true
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

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"true\")")
        }

        debug {
            buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"false\")")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":features:home"))
    implementation(project(":features:stocks"))
    implementation(project(":features:about"))
    implementation(project(":features:cryptos"))

    implementation(platform(Libs.Firebase.platform))
    implementation(Libs.Firebase.crashlytics)
}