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

package com.ferelin.buildPlugins

import com.android.build.gradle.BaseExtension
import com.ferelin.dependencies.Dependencies
import com.ferelin.dependencies.Plugins
import com.ferelin.dependencies.addImpl
import com.ferelin.dependencies.applyDefaultConfigure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

class SharedBuildPlugin : Plugin<Project> {

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
            apply(Plugins.androidLibrary)
            apply(Plugins.kotlinAndroid)
        }
    }

    private fun BaseExtension.applyAndroidConfigure() {
        applyDefaultConfigure()
    }

    private fun DependencyHandlerScope.implDependencies() {
        addImpl(Dependencies.kotlinLib)
    }
}