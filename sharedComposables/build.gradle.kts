import com.ferelin.Libs

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version com.ferelin.Libs.Compose.version
}

version = com.ferelin.Libs.Project.codeVersionName

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.uiTooling)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Libs.Compose.ui)
                implementation(Libs.Compose.util)
                implementation(Libs.Compose.material)
                implementation(Libs.Compose.glide)
                implementation(Libs.Accompanist.systemUiController)
            }
        }
    }
}
android {
    compileSdk = Libs.Project.currentSDK

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = Libs.Project.minSDK
        targetSdk = Libs.Project.currentSDK
    }
}