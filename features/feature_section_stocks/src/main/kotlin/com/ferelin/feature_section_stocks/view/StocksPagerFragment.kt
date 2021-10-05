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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
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
    }

    override fun initUx() {
        mViewBinding.cardViewSearch.setOnClick(mViewModel::onSearchCardClick)
    }

    companion object {

        fun newInstance(data: Any?): StocksPagerFragment {
            return StocksPagerFragment()
        }
    }
}