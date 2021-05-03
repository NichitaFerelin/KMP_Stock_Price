package com.ferelin.stockprice.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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