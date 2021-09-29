buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(com.ferelin.BuildPlugins.gradleTools)
        classpath(com.ferelin.BuildPlugins.kotlin)
        classpath(com.ferelin.BuildPlugins.googleServices)
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