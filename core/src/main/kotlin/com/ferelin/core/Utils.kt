package com.ferelin.core

import android.os.Looper

fun checkBackgroundThread() {
  check(Looper.getMainLooper() != Looper.myLooper()) {
    "Expected to be called on background thread but was ${Thread.currentThread().name}"
  }
}