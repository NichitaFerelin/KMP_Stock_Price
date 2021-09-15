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

package com.ferelin.dependencies

import com.android.build.gradle.BaseExtension
import groovyjarjarantlr4.v4.runtime.atn.ParserATNSimulator.debug
import org.gradle.api.JavaVersion

fun BaseExtension.applyDefaultConfigure() {

    compileSdkVersion(31)

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            val proguardFile = "proguard-rules.pro"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                proguardFile
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    val kotlinOptions = (this as org.gradle.api.plugins.ExtensionAware).extensions
    kotlinOptions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions>("kotlinOptions") {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs +
                ("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi") +
                ("-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi")
    }
}