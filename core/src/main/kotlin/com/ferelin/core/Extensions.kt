package com.ferelin.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun Context.startActivitySafety(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        /**/
    }
}