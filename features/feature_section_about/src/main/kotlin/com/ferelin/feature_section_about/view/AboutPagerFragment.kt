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

package com.ferelin.feature_section_about.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.params.AboutParams
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_section_about.R
import com.ferelin.feature_section_about.adapter.AboutPagerAdapter
import com.ferelin.feature_section_about.databinding.FragmentAboutPagerBinding
import com.ferelin.feature_section_about.viewModel.AboutPagerViewModel
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AboutPagerFragment : BaseFragment<FragmentAboutPagerBinding>() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<AboutPagerViewModel>

    private val viewModel: AboutPagerViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutPagerBinding
        get() = FragmentAboutPagerBinding::inflate

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 200L
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 200L
        }

        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        with(viewBinding) {
            textViewCompanyName.text = viewModel.aboutParams.companyName
            textViewCompanyTicker.text = viewModel.aboutParams.companyTicker
            imageViewStar.setImageResource(viewModel.favouriteIconRes)

            viewPager.adapter = AboutPagerAdapter(
                params = viewModel.aboutParams,
                fm = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle
            )
            viewPager.offscreenPageLimit = 5

            tabLayout.attachViewPager(
                viewPager,
                getString(R.string.titleProfile),
                getString(R.string.titleChart),
                getString(R.string.titleNews),
                getString(R.string.titleForecasts),
                getString(R.string.titleIdeas)
            )
        }
    }

    override fun initUx() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        viewBinding.imageViewBack.setOnClick(viewModel::onBackBtnClick)
        viewBinding.imageViewStar.setOnClick(viewModel::onFavouriteIconClick)
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observeFavouriteCompaniesUpdate() }
            launch { observeNetworkState() }
        }
    }

    override fun onDestroyView() {
        viewBinding.tabLayout.detachViewPager()
        super.onDestroyView()
    }

    private suspend fun observeFavouriteCompaniesUpdate() {
        viewModel.favouriteCompaniesUpdate.collect {
            withContext(Dispatchers.Main) {
                viewBinding.imageViewStar.setImageResource(viewModel.favouriteIconRes)
            }
        }
    }

    private suspend fun observeNetworkState() {
        viewModel.networkState.collect { isAvailable ->
            withContext(Dispatchers.Main) {
                if (isAvailable) {
                    showTempSnackbar(getString(R.string.messageNetworkAvailable))
                } else {
                    showSnackbar(getString(R.string.messageNetworkNotAvailable))
                }
            }
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[ABOUT_PARAMS_KEY]?.let { params ->
            if (params is AboutParams) {
                viewModel.aboutParams = params
                viewModel.isFavourite = params.isFavourite
            }
        }
    }

    companion object {

        private const val ABOUT_PARAMS_KEY = "about-params"

        fun newInstance(data: Any?): AboutPagerFragment {
            return AboutPagerFragment().also {
                if (data is AboutParams) {
                    it.arguments = bundleOf(ABOUT_PARAMS_KEY to data)
                }
            }
        }
    }
}