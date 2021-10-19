package com.ferelin

object BuildPlugins {
    const val gradleTools = "com.android.tools.build:gradle:${Versions.gradleTools}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinLib}"
    const val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
    const val versionsChecker =
        "com.github.ben-manes:gradle-versions-plugin:${Versions.versionsChecker}"
}