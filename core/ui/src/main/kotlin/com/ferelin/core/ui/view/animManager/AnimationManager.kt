package com.ferelin.core.ui.view.animManager

import android.view.animation.Animation

abstract class AnimationManager : Animation.AnimationListener {
  override fun onAnimationStart(animation: Animation?) = Unit
  override fun onAnimationEnd(animation: Animation?) = Unit
  override fun onAnimationRepeat(animation: Animation?) = Unit
}

fun Animation.invalidate() {
  setAnimationListener(null)
  cancel()
}