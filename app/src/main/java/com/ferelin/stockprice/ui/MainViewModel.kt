package com.ferelin.stockprice.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider(),
    private val mDataInteractor: DataInteractor,
    mApplication: Application
) : AndroidViewModel(mApplication) {

    init {
        initObservers()
    }

    private val mEventObserverCompanyChanged = MutableSharedFlow<AdaptiveCompany?>(1)
    val eventObserverCompanyChanged: SharedFlow<AdaptiveCompany?>
        get() = mEventObserverCompanyChanged

    private val mStateIsNetworkAvailable = MutableStateFlow(true)
    val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mStateIsNetworkAvailable

    val eventCriticalError: SharedFlow<String>
        get() = mDataInteractor.sharedPrepareCompaniesError

    val eventApiLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedApiLimitError

    private var mObserverCompanyCollectorJob: Job? = null

    private var mNetworkWasLost: Boolean = false

    fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { mDataInteractor.prepareData() }
            launch { collectNetworkState() }
            launch { collectCompanyUpdatesForObserver() }
        }
    }

    private suspend fun collectNetworkState() {
        mDataInteractor.stateIsNetworkAvailable.collect { onNetworkStateChanged(it) }
    }

    private suspend fun collectCompanyUpdatesForObserver() {
        mDataInteractor.stateCompanyForObserver.collect { onCompanyForObserverChanged(it) }
    }

    private suspend fun onCompanyForObserverChanged(company: AdaptiveCompany?) {
        if (company == null) {
            mObserverCompanyCollectorJob?.cancel()
            mEventObserverCompanyChanged.emit(null)
            return
        }

        mEventObserverCompanyChanged.emit(company)
        collectObserverCompanyUpdates(company)
    }

    private fun collectObserverCompanyUpdates(target: AdaptiveCompany) {
        mObserverCompanyCollectorJob?.cancel()
        mObserverCompanyCollectorJob = viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.sharedCompaniesUpdates
                .filter { it.data == target }
                .collect {
                    if (!isActive) {
                        cancel()
                    } else mEventObserverCompanyChanged.emit(target)
                }
        }
    }

    private suspend fun onNetworkStateChanged(isAvailable: Boolean) {
        viewModelScope.launch(mCoroutineContext.IO) {
            Log.d("Test", "$isAvailable")
            if (isAvailable) {
                /*
                * When the network is lost -> web socket breaks.
                * */
                if (mNetworkWasLost) {
                    restartWebSocket()
                }
                mStateIsNetworkAvailable.value = true
                mDataInteractor.openConnection().collect()
            } else {
                mNetworkWasLost = true
                mStateIsNetworkAvailable.value = false
            }
        }
    }

    private fun restartWebSocket() {
        mDataInteractor.prepareToWebSocketReconnection()
        mNetworkWasLost = false
    }
}