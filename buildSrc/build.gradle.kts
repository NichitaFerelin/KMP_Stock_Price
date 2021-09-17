plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("app-module-build") {
            id = "app-module-build"
            implementationClass = "com.ferelin.buildPlugins.AppBuildPlugin"
        }
        register("remote-module-build") {
            id = "remote-module-build"
            implementationClass = "com.ferelin.buildPlugins.RemoteBuildPlugin"
        }
        register("localSource-module-build") {
            id = "localSource-module-build"
            implementationClass = "com.ferelin.buildPlugins.LocalSourceBuildPlugin"
        }
        register("repository-module-build") {
            id = "repository-module-build"
            implementationClass = "com.ferelin.buildPlugins.RepositoryBuildPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}


dependencies {
    implementation(kotlin("android-extensions"))
    implementation(kotlin("gradle-plugin", "1.5.21"))
    implementation("com.android.tools.build:gradle:7.1.0-alpha11")
}