package com.ferelin.stockprice.base

import android.content.Context
import android.os.Bundle
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.*

/**
 * [BaseViewController] holds the logic for displaying any data.
 */
abstract class BaseViewController<out ViewAnimatorType : BaseViewAnimator, ViewBindingType>(
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