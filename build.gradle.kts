buildscript {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.1.2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${com.ferelin.Deps.kotlinVersion}")
    classpath("com.google.gms:google-services:4.3.10")
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    classpath("com.github.ben-manes:gradle-versions-plugin:0.41.0")
  }
}
allprojects {
  repositories {
    mavenCentral()
    google()
  }
}
tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}