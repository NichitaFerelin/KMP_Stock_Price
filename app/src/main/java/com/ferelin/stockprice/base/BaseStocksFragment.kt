package com.ferelin.stockprice.base

import android.os.Bundle
import android.view.View
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.utils.StockClickListener

abstract class BaseStocksFragment<out T : BaseStocksViewModel>
    : BaseFragment(), StockClickListener {

    protected abstract val mViewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComponents()
        initObservers()
    }

    protected open fun setUpComponents() {
        mViewModel.recyclerAdapter.setOnStockCLickListener(this)
    }

    protected open fun initObservers() {
        mViewModel.initObservers()
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onStockClicked(company: AdaptiveCompany) {
        mViewModel.onStockClicked(company)
    }
}