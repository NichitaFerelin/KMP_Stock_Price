import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose") version com.ferelin.Libs.composeDesktopVersion
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}

dependencies {
  implementation(project(":shared"))
  implementation(project(":sharedComposables"))
  implementation(compose.desktop.currentOs)
}

compose.desktop {
  application {
    mainClass = "AppKt"
  }
}