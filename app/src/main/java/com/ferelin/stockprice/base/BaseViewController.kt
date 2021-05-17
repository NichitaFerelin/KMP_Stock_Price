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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewbinding.ViewBinding
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.*

/**
 * [BaseViewController] holds the logic for displaying any data.
 */
abstract class BaseViewController<out ViewAnimatorType : BaseViewAnimator, ViewBindingType : ViewBinding>(
    protected val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) {
    var viewBinding: ViewBindingType? = null

    protected abstract val mViewAnimator: ViewAnimatorType

    protected var mContext: Context? = null
    protected var mViewLifecycleScope: LifecycleCoroutineScope? = null

    open fun onCreateFragment(fragment: Fragment) {}

    open fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        mContext = fragment.requireContext()
        mViewLifecycleScope = viewLifecycleScope
        mViewAnimator.loadAnimations(fragment.requireContext())
    }

    open fun onDestroyView() {
        mViewAnimator.invalidateAnimations()
        mViewLifecycleScope = null
        viewBinding = null
        mContext = null
    }

    /*
    * To avoid breaks of shared transition anim
    *  */
    fun postponeReferencesRemove(finally: () -> Unit) {
        CoroutineScope(mCoroutineContext.IO).launch {
            delay(200)
            withContext(mCoroutineContext.Main) {
                finally.invoke()
            }
            cancel()
        }
    }

    fun postponeTransitions(fragment: Fragment) {
        fragment.postponeEnterTransition()
        fragment.requireView().doOnPreDraw { fragment.startPostponedEnterTransition() }
    }
}