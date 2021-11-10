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
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.isOut
import com.ferelin.core.utils.launchAndRepeatWithViewLifecycle
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.view.BaseStocksFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_section_stocks.R
import com.ferelin.feature_section_stocks.adapter.CryptoItemDecoration
import com.ferelin.feature_section_stocks.adapter.StocksPagerAdapter
import com.ferelin.feature_section_stocks.databinding.FragmentStocksPagerBinding
import com.ferelin.feature_section_stocks.viewModel.StocksPagerViewModel
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StocksPagerFragment : BaseFragment<FragmentStocksPagerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStocksPagerBinding
        get() = FragmentStocksPagerBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<StocksPagerViewModel>

    private val viewModel: StocksPagerViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val backPressedCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewBinding.viewPager.currentItem != 0) {
                    viewBinding.viewPager.setCurrentItem(0, true)
                } else {
                    this.remove()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private val viewPagerChangeCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchTextStyles(position)
            }
        }
    }

    private var scaleInOut: Animator? = null
    private var scaleOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
            .apply { duration = 300L }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun initUi() {
        viewBinding.viewPager.adapter = StocksPagerAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        viewBinding.viewPager.registerOnPageChangeCallback(viewPagerChangeCallback)

        viewBinding.recyclerViewCrypto.apply {
            adapter = viewModel.cryptoAdapter
            addItemDecoration(CryptoItemDecoration(requireContext()))
            setHasFixedSize(true)
        }
    }

    override fun initUx() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        with(viewBinding) {
            textViewHintStocks.setOnClick(this@StocksPagerFragment::onHintStocksClick)
            textViewHintFavourite.setOnClick(this@StocksPagerFragment::onHistFavouritesClick)
            fab.setOnClick(this@StocksPagerFragment::onFabClick)
            imageSettings.setOnClick(viewModel::onSettingsClick)

            cardViewSearch.setOnClick {
                viewModel.onSearchCardClick(
                    sharedElement = viewBinding.toolbar,
                    name = requireContext().resources.getString(R.string.transitionSearchFragment)
                )
            }
        }
    }

    override fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            observeCryptoLoading()
        }
    }

    override fun onDestroyView() {
        viewBinding.viewPager.unregisterOnPageChangeCallback(viewPagerChangeCallback)
        scaleInOut?.invalidate()
        scaleOut?.invalidate()
        super.onDestroyView()
    }

    private suspend fun observeCryptoLoading() {
        viewModel.cryptoLoading.collect { isLoading ->
            withContext(Dispatchers.Main) {
                viewBinding.progressBarCrypto.isVisible = isLoading
            }
        }
    }

    private fun onHintStocksClick() {
        if (viewBinding.viewPager.currentItem != STOCK_SCREEN_POSITION) {
            viewBinding.viewPager.setCurrentItem(STOCK_SCREEN_POSITION, true)
        }
    }

    private fun onHistFavouritesClick() {
        if (viewBinding.viewPager.currentItem != FAVOURITES_SCREEN_POSITION) {
            viewBinding.viewPager.setCurrentItem(FAVOURITES_SCREEN_POSITION, true)
        }
    }

    private fun onFabClick() {
        val childFragments = this.childFragmentManager.fragments
        val currentChildPosition = viewBinding.viewPager.currentItem

        childFragments.getOrNull(currentChildPosition)?.let { child ->
            if (child is BaseStocksFragment<*, *>) {
                child.onFabClick()
                hideFab()
            }
        }
    }

    private fun switchTextStyles(selectedPosition: Int) {
        if (scaleInOut == null) {
            scaleInOut = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
        }

        with(viewBinding) {
            val itemToAnimate = if (selectedPosition == STOCK_SCREEN_POSITION) {
                setAsSelected(textViewHintStocks)
                setAsDefault(textViewHintFavourite)
                textViewHintStocks
            } else {
                setAsSelected(textViewHintFavourite)
                setAsDefault(textViewHintStocks)
                textViewHintFavourite
            }

            if (viewModel.lastSelectedPage != selectedPosition) {
                viewModel.lastSelectedPage = selectedPosition
                scaleInOut!!.setTarget(itemToAnimate)
                scaleInOut!!.start()
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
        if (scaleOut == null) {
            scaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
        }

        val animCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding.fab.visibility = View.INVISIBLE
                viewBinding.fab.isOut = false
            }
        }

        scaleOut!!.setAnimationListener(animCallback)
        viewBinding.fab.startAnimation(scaleOut!!)
    }

    companion object {

        private const val STOCK_SCREEN_POSITION = 0
        private const val FAVOURITES_SCREEN_POSITION = 1

        fun newInstance(data: Any?): StocksPagerFragment {
            return StocksPagerFragment()
        }
    }
}