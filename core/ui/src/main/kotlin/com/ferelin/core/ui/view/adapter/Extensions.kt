package com.ferelin.core.ui.view.adapter

import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.ui.view.animManager.AnimationManager
import com.ferelin.core.ui.viewData.utils.withTimer

fun RecyclerView.LayoutManager?.ifLinear(action: (LinearLayoutManager) -> Unit) {
  if (this is LinearLayoutManager) {
    action.invoke(this)
  }
}

fun RecyclerView.scrollToTopWithCustomAnim(
  fadeIn: Animation,
  fadeOut: Animation,
  customItemAnimator: RecyclerView.ItemAnimator? = null
) {
  // starts smooth scrolling to top -> fades out -> hard scroll to 20 position ->
  // fade in -> smooth scroll to 0
  val fadeInCallback = object : AnimationManager() {
    override fun onAnimationStart(animation: Animation?) {
      alpha = 1F
      smoothScrollToPosition(0)
    }

    override fun onAnimationEnd(animation: Animation?) {
      // To avoid graphic bug
      withTimer { itemAnimator = customItemAnimator }
    }
  }
  fadeIn.setAnimationListener(fadeInCallback)

  val fadeOutCallback = object : AnimationManager() {
    override fun onAnimationStart(animation: Animation?) {
      // To avoid graphic bug
      itemAnimator = null
      smoothScrollBy(0, -height)
    }

    override fun onAnimationEnd(animation: Animation?) {
      alpha = 0F
      scrollToPosition(20)
      startAnimation(fadeIn)
    }
  }
  fadeOut.setAnimationListener(fadeOutCallback)
  startAnimation(fadeOut)
}