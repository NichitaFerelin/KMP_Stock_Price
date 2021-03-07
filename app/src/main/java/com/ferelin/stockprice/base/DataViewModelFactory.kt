package com.ferelin.stockprice.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.common.StocksViewModel

@Suppress("UNCHECKED_CAST")
class DataViewModelFactory(private val dataInteractor: DataInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StocksViewModel::class.java) -> {
                StocksViewModel(dataInteractor) as T
            }
            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}