package com.ferelin.stockprice.base

import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor

/**
 * [BaseViewModel] holds the logic of data loading using [mDataInteractor].
 */
abstract class BaseViewModel(
    protected val mCoroutineContext: CoroutineContextProvider,
    protected val mDataInteractor: DataInteractor
) : ViewModel() {

    /*
    * Add your observers here.
    * */
    protected abstract fun initObserversBlock()

    /*
    * To avoid calling non-final function in init-block.
    * */
    private var mWasInitialized = false

    fun initObservers() {
        if (!mWasInitialized) {
            initObserversBlock()
            mWasInitialized = true
        }
    }

    fun triggerCreate() {
        // Do nothing. Used to trigger lazy initialization.
    }
}