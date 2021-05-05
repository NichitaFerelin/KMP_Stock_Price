package com.ferelin.stockprice.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
class MainViewModel(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider(),
    private val mDataInteractor: DataInteractor,
    mApplication: Application
) : AndroidViewModel(mApplication) {

    init {
        initObservers()
    }

    private val mActionShowDialogError = MutableSharedFlow<String>()
    val actionShowDialogError: SharedFlow<String>
        get() = mActionShowDialogError

    private val mActionShowNetworkError = MutableStateFlow(false)
    val actionShowNetworkError: StateFlow<Boolean>
        get() = mActionShowNetworkError

    private val mActionShowApiLimitError = MutableSharedFlow<Unit>()
    val actionShowApiLimitError: SharedFlow<Unit>
        get() = mActionShowApiLimitError

    private var mNetworkWasLost: Boolean = false

    @FlowPreview
    fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.sharedPrepareCompaniesError
                    .collect { mActionShowDialogError.emit(it) }
            }
            launch {
                mDataInteractor.stateIsNetworkAvailable
                    .collect { onNetworkStateChanged(it) }
            }
            launch {
                mDataInteractor.sharedApiLimitError
                    .collect { mActionShowApiLimitError.emit(Unit) }
            }
            launch { mDataInteractor.prepareData() }

            launch {
                mDataInteractor.stateCompanyForObserver.collect { onCompanyForObserverChanged(it) }
            }
        }
    }

    private val mEventOnObserverCompanyMessage = MutableSharedFlow<AdaptiveCompany?>(1)
    val eventOnObserverCompanyMessage: SharedFlow<AdaptiveCompany?>
        get() = mEventOnObserverCompanyMessage

    private var mObserverJob: Job? = null

    private suspend fun onCompanyForObserverChanged(company: AdaptiveCompany?) {
        if (company == null) {
            mObserverJob?.cancel()
            mEventOnObserverCompanyMessage.emit(null)
            return
        }

        mEventOnObserverCompanyMessage.emit(company)
        mObserverJob?.cancel()
        mObserverJob = viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.sharedCompaniesUpdates
                .filter { it.data == company }
                .collect {
                    if (!isActive) {
                        cancel()
                    } else {
                        mEventOnObserverCompanyMessage.emit(company)
                    }
                }
        }
    }

    private suspend fun onNetworkStateChanged(isAvailable: Boolean) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (isAvailable) {
                /*
                * When the network is lost -> web socket breaks.
                * */
                if (mNetworkWasLost) {
                    restartWebSocket()
                }
                mActionShowNetworkError.value = false
                mDataInteractor.openConnection().collect()
            } else {
                mNetworkWasLost = true
                mActionShowNetworkError.value = true
            }
        }
    }

    private fun restartWebSocket() {
        mDataInteractor.prepareToWebSocketReconnection()
        mNetworkWasLost = false
    }
}