package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirstTimeLaunchStateWorker {

    private val mFirstTimeLaunchState = MutableStateFlow<Boolean?>(null)
    val firstTimeLaunchState: StateFlow<Boolean?>
        get() = mFirstTimeLaunchState

    fun onResponse(response: LocalInteractorResponse) {
        mFirstTimeLaunchState.value = when (response) {
            is LocalInteractorResponse.Success -> response.firstTimeLaunch
            else -> true
        }
    }
}