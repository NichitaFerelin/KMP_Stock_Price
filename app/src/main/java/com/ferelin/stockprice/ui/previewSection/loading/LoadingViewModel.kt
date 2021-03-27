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

    private val mMoveToNextScreen = MutableStateFlow<Boolean?>(null)
    val moveToNextScreen: StateFlow<Boolean?>
        get() = mMoveToNextScreen

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.firstTimeLaunchState
                .filter { it != null }
                .collect { mMoveToNextScreen.value = it }
        }
    }
}