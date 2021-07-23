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

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.implUiDependenciesPack() {
    addImpl(Dependencies.fragments)
    addImpl(Dependencies.viewPager)
    addImpl(Dependencies.constraintLayout)
    addImpl(Dependencies.material)
}

fun DependencyHandlerScope.implNetworkDependenciesPack() {
    addImpl(Dependencies.retrofit)
    addImpl(Dependencies.retrofitMoshiConverter)
    addImpl(Dependencies.okHttp)
    addImpl(Dependencies.okHttpInterceptor)
    addImpl(Dependencies.moshi)
}

fun DependencyHandlerScope.implFirebaseDependenciesPack() {
    add("implementation", platform(Dependencies.firebasePlatform))
    addImpl(Dependencies.firebaseAnalyticsKtx)
    addImpl(Dependencies.firebaseAuthenticationKtx)
    addImpl(Dependencies.firebaseDatabaseKtx)
}

fun DependencyHandlerScope.implComposeDependenciesPack() {
    addImpl(Dependencies.composeUi)
    addImpl(Dependencies.composeUiTooling)
    addImpl(Dependencies.composeFoundation)
    addImpl(Dependencies.composeMaterial)
    addImpl(Dependencies.composeRuntimeLivedata)
}

fun DependencyHandlerScope.addImpl(dependencyNotation: String) {
    add("implementation", dependencyNotation)
}

fun DependencyHandlerScope.addProjectImpl(dependencyNotation: String) {
    add("implementation", project(dependencyNotation))
}

fun DependencyHandlerScope.kapt(dependencyNotation: String) {
    add("kapt", dependencyNotation)
}

fun DependencyHandlerScope.addTestImpl(dependencyNotation: String) {
    add("testImplementation", dependencyNotation)
}

fun DependencyHandlerScope.addAndroidTestImpl(dependencyNotation: String) {
    add("androidTestImplementation", dependencyNotation)
}