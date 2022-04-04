import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose") version "1.1.1"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":shared"))
  implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

application {
  mainClass.set("MainKt")
}