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

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ferelin.core.adapter.stocks.StockItemAnimator
import com.ferelin.core.adapter.stocks.StockItemDecoration
import com.ferelin.core.adapter.stocks.StockViewHolder
import com.ferelin.core.utils.swipe.SwipeActionCallback
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.core.viewModel.StocksMode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class BaseStocksFragment<VB : ViewBinding, VM : BaseStocksViewModel> : BaseFragment<VB>() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<VM>

    abstract val mViewModel: VM
    abstract val mStocksMode: StocksMode

    protected var recyclerView: RecyclerView? = null

    override fun initUi() {
        recyclerView?.apply {
            adapter = mViewModel.stocksAdapter
            itemAnimator = StockItemAnimator()
            addItemDecoration(StockItemDecoration(requireContext()))

            ItemTouchHelper(
                SwipeActionCallback(
                    onHolderRebound = this@BaseStocksFragment::onHolderRebound,
                    onHolderUntouched = this@BaseStocksFragment::onHolderUntouched
                )
            ).attachToRecyclerView(this)
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            withContext(mDispatchersProvider.IO) {
                launch { mViewModel.loadStocks(mStocksMode) }
                launch { mViewModel.companiesStockPriceUpdates.collect() }
                launch { mViewModel.favouriteCompaniesUpdates.collect() }
                launch { mViewModel.actualStockPrice.collect() }
            }
        }
    }

    override fun onDestroyView() {
        recyclerView = null
        super.onDestroyView()
    }

    private fun onHolderRebound(stockViewHolder: StockViewHolder) {
    }

    private fun onHolderUntouched(stockViewHolder: StockViewHolder, isRebounded: Boolean) {

    }
}