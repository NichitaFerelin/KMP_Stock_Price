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

package com.ferelin.feature_section_stocks.view

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.view.BaseStocksFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_section_stocks.R
import com.ferelin.feature_section_stocks.adapter.StocksPagerAdapter
import com.ferelin.feature_section_stocks.databinding.FragmentStocksPagerBinding
import com.ferelin.feature_section_stocks.viewModel.StocksPagerViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import javax.inject.Inject

class StocksPagerFragment : BaseFragment<FragmentStocksPagerBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStocksPagerBinding
        get() = FragmentStocksPagerBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<StocksPagerViewModel>

    private val mViewModel: StocksPagerViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val mBackPressedCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mViewBinding.viewPager.currentItem != 0) {
                    mViewBinding.viewPager.setCurrentItem(0, true)
                } else {
                    this.remove()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private val mViewPagerChangeCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchTextStyles(position)
            }
        }
    }

    private var mScaleInOut: Animator? = null
    private var mScaleOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
        exitTransition = MaterialElevationScale(false).apply {
            duration = 200L
        }
    }

    override fun initUi() {
        mViewBinding.viewPager.adapter = StocksPagerAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        mViewBinding.viewPager.registerOnPageChangeCallback(mViewPagerChangeCallback)
    }

    override fun initUx() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )

        with(mViewBinding) {
            textViewHintStocks.setOnClick(this@StocksPagerFragment::onHintStocksClick)
            textViewHintFavourite.setOnClick(this@StocksPagerFragment::onHistFavouritesClick)
            fab.setOnClick(this@StocksPagerFragment::onFabClick)

            cardViewSearch.setOnClick {
                mViewModel.onSearchCardClick(
                    sharedElement = mViewBinding.toolbar,
                    name = requireContext().resources.getString(R.string.transitionSearchFragment)
                )
            }
        }
    }

    override fun onDestroyView() {
        mViewBinding.viewPager.unregisterOnPageChangeCallback(mViewPagerChangeCallback)
        mScaleInOut?.invalidate()
        mScaleOut?.invalidate()
        super.onDestroyView()
    }

    private fun onHintStocksClick() {
        if (mViewBinding.viewPager.currentItem != sStockPosition) {
            mViewBinding.viewPager.setCurrentItem(sStockPosition, true)
        }
    }

    private fun onHistFavouritesClick() {
        if (mViewBinding.viewPager.currentItem != sFavouriteStocksPosition) {
            mViewBinding.viewPager.setCurrentItem(sFavouriteStocksPosition, true)
        }
    }

    private fun onFabClick() {
        val childFragments = this.childFragmentManager.fragments
        val currentChildPosition = mViewBinding.viewPager.currentItem

        childFragments.getOrNull(currentChildPosition)?.let { child ->
            if (child is BaseStocksFragment<*, *>) {
                child.onFabClick()
                hideFab()
            }
        }
    }

    private fun switchTextStyles(selectedPosition: Int) {
        if (mScaleInOut == null) {
            mScaleInOut = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
        }

        with(mViewBinding) {
            val itemToAnimate = if (selectedPosition == sStockPosition) {
                setAsSelected(textViewHintStocks)
                setAsDefault(textViewHintFavourite)
                textViewHintStocks
            } else {
                setAsSelected(textViewHintFavourite)
                setAsDefault(textViewHintStocks)
                textViewHintFavourite
            }

            if (mViewModel.lastSelectedPage != selectedPosition) {
                mViewModel.lastSelectedPage = selectedPosition
                mScaleInOut!!.setTarget(itemToAnimate)
                mScaleInOut!!.start()
            }
        }
    }

    private fun setAsDefault(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH2Shadowed)
    }

    private fun setAsSelected(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH1)
    }

    private fun hideFab() {
        if (mScaleOut == null) {
            mScaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
        }

        val animCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                mViewBinding.fab.visibility = View.INVISIBLE
                mViewBinding.fab.scaleX = 1.0F
                mViewBinding.fab.scaleY = 1.0F
            }
        }

        mScaleOut!!.setAnimationListener(animCallback)
        mViewBinding.fab.startAnimation(mScaleOut!!)
    }

    companion object {

        private const val sStockPosition = 0
        private const val sFavouriteStocksPosition = 1

        fun newInstance(data: Any?): StocksPagerFragment {
            return StocksPagerFragment()
        }
    }
}