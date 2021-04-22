package com.ferelin.stockprice.base

import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor

/*
* BaseDataViewModel is the fundament for view models where is needed data source.
* */
abstract class BaseDataViewModel(
    protected val mCoroutineContext: CoroutineContextProvider,
    protected val mDataInteractor: DataInteractor
) : ViewModel() {

    /*
    * Will be called at Add your observers here.
    * */
    protected abstract fun initObserversBlock()

    /*
    * To avoid multiple call of initObserversBlock().
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