package com.ferelin.stockprice.ui.stocksSection.stocksPager

import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor

class StocksPagerViewModel(
    contextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseViewModel(contextProvider, dataInteractor) {

    override fun initObserversBlock() {
        // Do nothing.
    }
}