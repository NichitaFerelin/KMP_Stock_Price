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

        register("local-module-build") {
            id = "local-module-build"
            implementationClass = "com.ferelin.buildPlugins.LocalBuildPlugin"
        }

        register("repository-module-build") {
            id = "repository-module-build"
            implementationClass = "com.ferelin.buildPlugins.RepositoryBuildPlugin"
        }

        register("shared-module-build") {
            id = "shared-module-build"
            implementationClass = "com.ferelin.buildPlugins.SharedBuildPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}


dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("android-extensions"))
    implementation(kotlin("gradle-plugin", "1.5.10"))
    implementation("com.android.tools.build:gradle:7.1.0-alpha03")
}