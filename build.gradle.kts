buildscript {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
  dependencies {
    classpath(com.ferelin.Libs.Plugins.gradle)
    classpath(com.ferelin.Libs.Plugins.kotlin)
    classpath(com.ferelin.Libs.Plugins.google)
    classpath(com.ferelin.Libs.Plugins.firebase)
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