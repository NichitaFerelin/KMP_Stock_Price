buildscript {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath(com.ferelin.Libs.Plugins.gradle)
    classpath(com.ferelin.Libs.Plugins.kotlin)
    classpath(com.ferelin.Libs.Plugins.firebase)
    classpath(com.ferelin.Libs.Plugins.google)
    classpath(com.ferelin.Libs.SqlDelight.plugin)
  }
}
allprojects {
  repositories {
    google()
    mavenCentral()
  }
}
tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}