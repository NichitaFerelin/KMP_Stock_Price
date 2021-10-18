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

package com.ferelin.core.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ferelin.core.R
import com.ferelin.shared.DispatchersProvider
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = checkNotNull(_viewBinding)

    @Inject
    lateinit var dispatchersProvider: DispatchersProvider

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

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
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
            .show()
    }

    fun showTempSnackbar(message: String) {
        Snackbar
            .make(viewBinding.root, message, Snackbar.LENGTH_LONG)
            .show()
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