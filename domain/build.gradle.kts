import com.ferelin.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-kapt")
}

android {
  compileSdk = Deps.currentSDK
  defaultConfig {
    minSdk = Deps.minSDK
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
  implementation(project(":shared"))

  implementation(Deps.kotlinLib)
  implementation(Deps.kotlinCoroutines)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)

  // Tests
  testImplementation(project(":data:data_local"))

  testImplementation(Deps.dagger)
  kaptTest(Deps.daggerCompilerKapt)

  testImplementation(Deps.roomKtx)
  testImplementation(Deps.roomRuntime)
  kaptTest(Deps.roomCompilerKapt)

  // Build Room Exception without live data implementation:
  // DefClassNotFound androidx/lifecycle/livedata
  testImplementation(Deps.roomTestLiveData)

  testImplementation(Deps.testRobolectric)
  testImplementation(Deps.testJunitKtx)
  testImplementation(Deps.testCoroutines)
  testImplementation(Deps.testMockito)
}