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

    @FlowPreview
    fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.prepareData(mApplication)
                mDataInteractor.openConnection().collect()
            }
            launch {
                mDataInteractor.prepareCompaniesErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowDialogError.emit(it) }
            }
        }
    }
}