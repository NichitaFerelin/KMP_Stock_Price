package com.ferelin.stockprice.ui.previewSection.loading

import androidx.lifecycle.viewModelScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class LoadingViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mIsFirstTimeLaunchState = MutableStateFlow<Boolean?>(null)
    val isFirstTimeLaunchState: StateFlow<Boolean?>
        get() = mIsFirstTimeLaunchState

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            collectFirstTimeLaunchState()
        }
    }

    private suspend fun collectFirstTimeLaunchState() {
        mDataInteractor.stateFirstTimeLaunch
            .filter { it != null }
            .collect { mIsFirstTimeLaunchState.value = it }
    }
}