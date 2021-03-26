package com.ferelin.stockprice.base

import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor

abstract class BaseViewModel(
    protected val mCoroutineContext: CoroutineContextProvider,
    protected val mDataInteractor: DataInteractor
) : ViewModel() {

    protected abstract fun initObserversBlock()

    private var mWasInitialized = false

    fun initObservers() {
        if (!mWasInitialized) {
            initObserversBlock()
            mWasInitialized = true
        }
    }

    fun triggerCreate() {}
}