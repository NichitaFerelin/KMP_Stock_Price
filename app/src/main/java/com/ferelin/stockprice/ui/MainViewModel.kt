package com.ferelin.stockprice.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@FlowPreview
class MainViewModel(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider(),
    private val mDataInteractor: DataInteractor,
    private val mApplication: Application
) : AndroidViewModel(mApplication) {

    init {
        initObservers()
    }

    private val mActionShowDialogError = MutableSharedFlow<String>()
    val actionShowDialogError: SharedFlow<String>
        get() = mActionShowDialogError

    private val mActionShowNetworkError = MutableSharedFlow<Unit>()
    val actionShowNetworkError: SharedFlow<Unit>
        get() = mActionShowNetworkError

    private val mActionShowApiLimitError = MutableSharedFlow<Unit>()
    val actionShowApiLimitError: SharedFlow<Unit>
        get() = mActionShowApiLimitError

    private var mNetworkWasLost: Boolean = false

    @FlowPreview
    fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.prepareCompaniesErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowDialogError.emit(it) }
            }
            launch {
                mDataInteractor.isNetworkAvailableState
                    .collect { onNetworkStateChanged(it) }
            }
            launch {
                mDataInteractor.apiLimitError
                    .filter { it }
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
                mDataInteractor.openConnection().collect()
            } else {
                mNetworkWasLost = true
                mActionShowNetworkError.emit(Unit)
            }
        }
    }

    private fun restartWebSocket() {
        mDataInteractor.prepareToWebSocketReconnection()
        mNetworkWasLost = false
    }
}