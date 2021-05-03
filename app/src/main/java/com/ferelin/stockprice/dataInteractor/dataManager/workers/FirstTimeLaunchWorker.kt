package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [FirstTimeLaunchWorker] providing [mStateFirstTimeLaunch]
 */
class FirstTimeLaunchWorker {

    private val mStateFirstTimeLaunch = MutableStateFlow<Boolean?>(null)
    val stateFirstTimeLaunch: StateFlow<Boolean?>
        get() = mStateFirstTimeLaunch

    fun onResponse(response: LocalInteractorResponse) {
        mStateFirstTimeLaunch.value = when (response) {
            is LocalInteractorResponse.Success -> response.firstTimeLaunch
            else -> true
        }
    }
}