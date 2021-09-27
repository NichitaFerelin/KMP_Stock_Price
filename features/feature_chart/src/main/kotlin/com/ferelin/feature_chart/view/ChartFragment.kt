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

package com.ferelin.feature_chart.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.feature_chart.databinding.FragmentChartBinding
import com.ferelin.feature_chart.viewData.PastPriceLoadState
import com.ferelin.feature_chart.viewData.StockPriceLoadState
import com.ferelin.feature_chart.viewModel.ChartViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO temp args
val companyId = 1
val companyTicker = "MSFT"

class ChartFragment : BaseFragment<FragmentChartBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChartBinding
        get() = FragmentChartBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ChartViewModel>

    private val mViewModel: ChartViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch { observePastPrice() }
            launch {
                observeActualStockPrice()
                observeLiveTimePrice()
            }
        }
    }

    private suspend fun observePastPrice() {
        mViewModel.pastPriceLoadState.collect { pastPriceLoad ->
            when (pastPriceLoad) {
                is PastPriceLoadState.Loaded -> {
                    // update UI
                }
                is PastPriceLoadState.Loading -> {

                }
                is PastPriceLoadState.Error -> {

                }
                is PastPriceLoadState.None -> {
                    mViewModel.loadPastPrices(companyId, companyTicker)
                }
            }
        }
    }

    private suspend fun observeActualStockPrice() {
        mViewModel.stockPriceLoad
            .take(1)
            .collect { stockPriceLoad ->
                when (stockPriceLoad) {
                    is StockPriceLoadState.Loaded -> {
                        // update UI
                    }
                    is StockPriceLoadState.Loading -> {

                    }
                    is StockPriceLoadState.None -> {
                        mViewModel.loadActualStockPrice(companyId)
                    }
                }
            }
    }

    private suspend fun observeLiveTimePrice() {
        mViewModel.observeLiveTimePrice(companyId).collect {
            // update ui
        }
    }
}