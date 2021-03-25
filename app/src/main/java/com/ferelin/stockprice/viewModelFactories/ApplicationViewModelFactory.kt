package com.ferelin.stockprice.viewModelFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.MainViewModel
import kotlinx.coroutines.FlowPreview

@Suppress("UNCHECKED_CAST")
class ApplicationViewModelFactory(
    private val mCoroutineContext: CoroutineContextProvider,
    private val mDataInteractor: DataInteractor,
    private val mApplication: Application
) : DataViewModelFactory(mCoroutineContext, mDataInteractor) {

    @FlowPreview
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(mCoroutineContext, mDataInteractor, mApplication) as T
            }
            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}