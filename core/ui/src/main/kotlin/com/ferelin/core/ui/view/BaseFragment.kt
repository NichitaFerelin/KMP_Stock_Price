package com.ferelin.core.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ferelin.core.ui.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
  abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

  private var _viewBinding: VB? = null
  protected val viewBinding: VB
    get() = checkNotNull(_viewBinding)

  private var activeSnackbar: Snackbar? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _viewBinding = bindingInflater.invoke(inflater, container, false)
    return viewBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    postponeTransitions(this)
    initUi()
    initUx()
    initObservers()
  }

  override fun onPause() {
    activeSnackbar?.dismiss()
    super.onPause()
  }

  override fun onDestroyView() {
    _viewBinding = null
    super.onDestroyView()
  }

  open fun initUi() {
    // Override in subclasses
  }

  open fun initUx() {
    // Override in subclasses
  }

  open fun initObservers() {
    // Override in subclasses
  }

  fun showSnackbar(message: String) {
    Snackbar
      .make(viewBinding.root, message, Snackbar.LENGTH_INDEFINITE)
      .setAction(R.string.hintOk) { /*dismiss by default*/ }
      .also { activeSnackbar = it }
      .show()
  }

  fun showActionSnackbar(message: String, actionMessage: String, action: () -> Unit) {
    Snackbar
      .make(viewBinding.root, message, Snackbar.LENGTH_INDEFINITE)
      .setAction(actionMessage) { action.invoke() }
      .also { activeSnackbar = it }
      .show()
  }

  fun showTempSnackbar(message: String) {
    activeSnackbar = Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_LONG)
    activeSnackbar!!.show()
  }

  fun hideKeyboard() {
    with(requireActivity()) {
      val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
  }

  fun showKeyboard(view: View) {
    with(requireActivity()) {
      currentFocus?.clearFocus()
      view.requestFocus()
      (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
  }

  private fun postponeTransitions(fragment: Fragment) {
    fragment.postponeEnterTransition()
    fragment
      .requireView()
      .doOnPreDraw { fragment.startPostponedEnterTransition() }
  }
}