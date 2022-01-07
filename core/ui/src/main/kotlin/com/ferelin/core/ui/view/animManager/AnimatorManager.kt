package com.ferelin.core.ui.view.animManager

import android.animation.Animator

fun Animator.invalidate() {
  removeAllListeners()
  cancel()
}