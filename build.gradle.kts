buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(com.ferelin.Libs.Project.gradlePlugin)
        classpath(com.ferelin.Libs.Kotlin.plugin)
        classpath(com.ferelin.Libs.Firebase.crashlyticsPlugin)
        classpath(com.ferelin.Libs.SqlDelight.plugin)
    }
}
allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}