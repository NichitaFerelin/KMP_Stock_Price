import com.ferelin.Base
import com.ferelin.Dependencies
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = freeCompilerArgs +
                ("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(Projects.shared))

    implementation(Dependencies.kotlinLib)
    implementation(Dependencies.kotlinCoroutines)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)

    // Tests
    testImplementation(project(Projects.dataLocal))

    testImplementation(Dependencies.dagger)
    kaptTest(Dependencies.daggerCompilerKapt)

    testImplementation(Dependencies.roomKtx)
    testImplementation(Dependencies.roomRuntime)
    kaptTest(Dependencies.roomCompilerKapt)

    // Build Room Exception without live data implementation:
    // DefClassNotFound androidx/lifecycle/livedata
    testImplementation(Dependencies.roomTestLiveData)

    testImplementation(Dependencies.testRobolectric)
    testImplementation(Dependencies.testJunitKtx)
    testImplementation(Dependencies.testCoroutines)
    testImplementation(Dependencies.testMockito)
}