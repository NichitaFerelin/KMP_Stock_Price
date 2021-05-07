package com.ferelin.stockprice.ui.previewSection.loading

import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.flow.StateFlow

class LoadingViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    val isFirstTimeLaunchState: StateFlow<Boolean?>
        get() = mDataInteractor.stateFirstTimeLaunch

    override fun initObserversBlock() {
        // Do nothing.
    }
}