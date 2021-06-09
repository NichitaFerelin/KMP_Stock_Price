package com.ferelin.stockprice.base

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

import android.content.Context
import android.os.Bundle
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.utils.withTimerOnUi
import javax.inject.Inject

/**
 * [BaseViewController] represents a class that controls the behavior of the view and all
 * associated logic using [mViewBinding].
 *
 * [mViewAnimator] provides ability to run animations.
 *
 * To control view lifecycle class uses [onViewCreated] and [onDestroyView] methods.
 */
abstract class BaseViewController<out ViewAnimatorType : BaseViewAnimator, ViewBindingType : ViewBinding> {

    protected abstract val mViewAnimator: ViewAnimatorType

    private var mViewBinding: ViewBindingType? = null
    val viewBinding: ViewBindingType
        get() = checkNotNull(mViewBinding)

    private var mContext: Context? = null
    protected val context: Context
        get() = checkNotNull(mContext)

    @Inject
    lateinit var mCoroutineContext: CoroutineContextProvider

    open fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        mContext = fragment.requireContext()
        mViewAnimator.loadAnimations(fragment.requireContext())
    }

    open fun onDestroyView() {
        mViewAnimator.invalidateAnimations()
        mViewBinding = null
        mContext = null
    }

    /**
     * Notifies about fragment creating
     */
    open fun onCreateFragment(fragment: Fragment) {
        // Do nothing
    }

    /**
     * Can be used to save arguments in bundle object
     */
    open fun onSaveInstanceState(outState: Bundle) {
        // Do nothing
    }

    fun setViewBinding(viewBinding: ViewBindingType) {
        mViewBinding = viewBinding
    }

    /*
    * To avoid breaks of shared transition at the exit of the fragment
    *  */
    fun postponeReferencesRemove(finally: () -> Unit) {
        withTimerOnUi { finally.invoke() }
    }

    fun postponeTransitions(fragment: Fragment) {
        fragment.postponeEnterTransition()
        fragment.requireView().doOnPreDraw { fragment.startPostponedEnterTransition() }
    }
}