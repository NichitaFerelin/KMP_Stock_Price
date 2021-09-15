buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(com.ferelin.dependencies.Dependencies.gradleTools)
        classpath(com.ferelin.dependencies.Dependencies.gradleKotlinPlugin)
        classpath(com.ferelin.dependencies.Dependencies.googleServices)
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