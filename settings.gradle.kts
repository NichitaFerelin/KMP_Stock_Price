pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

rootProject.name = "Stock Price"
include(
  ":androidApp",
  ":desktopApp",
  ":shared",
  ":sharedComposables"
)