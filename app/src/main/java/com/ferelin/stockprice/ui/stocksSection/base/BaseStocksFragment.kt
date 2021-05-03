package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.ui.stocksSection.common.StockClickListener
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStocksFragment<
        ViewBinding,
        out ViewModel : BaseStocksViewModel,
        out ViewController : BaseStocksViewController<ViewBinding>>
    : BaseFragment<ViewBinding, ViewModel, ViewController>(), StockClickListener {

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewModel.stocksRecyclerAdapter.setOnStockCLickListener(this)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectEventCompanyChanged() }
            launch { collectEventOnFabClicked() }
        }
    }

    override fun onStockClicked(stockViewHolder: StockViewHolder, company: AdaptiveCompany) {
        mViewController.onStockClicked(stockViewHolder, company)
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onHolderRebound(stockViewHolder: StockViewHolder) {
        mViewController.onStockHolderRebound(stockViewHolder)
    }

    override fun onHolderUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean) {
        mViewController.onStockHolderUntouched(
            stockViewHolder,
            rebounded,
            onAccepted = { mViewModel.onFavouriteIconClicked(it) }
        )
    }

    private suspend fun collectEventCompanyChanged() {
        mViewModel.eventCompanyChanged.collect { changedCompany ->
            withContext(mCoroutineContext.Main) {
                mViewController.onCompanyChanged(changedCompany)
            }
        }
    }

    /*
    * Is available only for stockPagerFragment child
    * */
    private suspend fun collectEventOnFabClicked() {
        if (parentFragment is StocksPagerFragment) {
            (parentFragment as StocksPagerFragment).eventOnFabClicked.collect {
                withContext(mCoroutineContext.Main) {
                    mViewController.onFabClicked()
                }
            }
        }
    }
}