package com.ferelin.buildPlugins

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.gradle.BaseExtension
import com.ferelin.dependencies.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

class AppBuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.applyPlugins()

        val androidExtension = project.extensions.getByName("android")
        if (androidExtension is BaseExtension) {
            androidExtension.apply { applyAndroidConfigure() }
        }

        project.dependencies { implDependencies() }
    }

    private fun Project.applyPlugins() {
        project.plugins.apply {
            apply(Plugins.androidApplication)
            apply(Plugins.kotlinAndroid)
            apply(Plugins.kotlinKapt)
        }
    }

    private fun BaseExtension.applyAndroidConfigure() {

        applyDefaultConfigure()

        defaultConfig {
            applicationId = "com.ferelin.stockprice"
            minSdk = 21
            targetSdk = 30
            versionCode = 7
            versionName = "2.1"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildFeatures.apply {
            viewBinding = true
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = Versions.compose
        }
    }

    private fun DependencyHandlerScope.implDependencies() {
        addProjectImpl(Modules.repository)
        addProjectImpl(Modules.shared)

        addImpl(Dependencies.kotlinLib)
        addImpl(Dependencies.kotlinCoroutines)
        addImpl(Dependencies.androidCore)

        addImpl(Dependencies.appCompat)
        addImpl(Dependencies.browser)
        implUiDependenciesPack()
        implComposeDependenciesPack()

        addImpl(Dependencies.dagger)
        kapt(Dependencies.daggerCompilerKapt)

        addImpl(Dependencies.glide)
        kapt(Dependencies.glideCompilerKapt)

        /**
         * NEXT DEPENDENCIES IS FOR DI AND TESTS
         * */
        addProjectImpl(Modules.local)
        addProjectImpl(Modules.remote)

        addImpl(Dependencies.roomKtx)
        addImpl(Dependencies.roomRuntime)

        implNetworkDependenciesPack()
        implFirebaseDependenciesPack()

        addAndroidTestImpl(Dependencies.testCoreKtx)
        addAndroidTestImpl(Dependencies.testJunitKtx)
        addAndroidTestImpl(Dependencies.testRunner)
        addAndroidTestImpl(Dependencies.testEspressoCore)
        addAndroidTestImpl(Dependencies.testEspressoContrib)
        addAndroidTestImpl(Dependencies.testUiAutomator)
    }
}