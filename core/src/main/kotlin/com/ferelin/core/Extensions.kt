package com.ferelin.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun <T> List<T>.itemsNotIn(param: List<T>): List<T> {
  val itemsNotIn = mutableListOf<T>()
  this.forEach { item ->
    if (item !in param) {
      itemsNotIn.add(item)
    }
  }
  return itemsNotIn
}

fun Context.startActivitySafety(intent: Intent) {
  try {
    startActivity(intent)
  } catch (e: ActivityNotFoundException) {
    /**/
  }
}