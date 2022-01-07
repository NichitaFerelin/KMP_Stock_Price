package com.ferelin.core.ui.view

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val Int.px: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Context.isLandscapeOrientation: Boolean
  get() = this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun View.setOnClick(listener: (() -> Unit)) {
  setOnClickListener { listener.invoke() }
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
  activeState: Lifecycle.State = Lifecycle.State.STARTED,
  crossinline block: suspend CoroutineScope.() -> Unit
) {
  viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.lifecycle.repeatOnLifecycle(activeState) {
      block()
    }
  }
}