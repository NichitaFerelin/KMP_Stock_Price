package com.ferelin.stockprice.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.favourite.FavouriteViewModel
import com.ferelin.stockprice.ui.search.SearchViewModel
import com.ferelin.stockprice.ui.stocks.StocksViewModel
import kotlinx.coroutines.FlowPreview

@Suppress("UNCHECKED_CAST")
open class DataViewModelFactory(
    private val mCoroutineContext: CoroutineContextProvider,
    private val mDataInteractor: DataInteractor
) : ViewModelProvider.Factory {

    @FlowPreview
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StocksViewModel::class.java) -> {
                StocksViewModel(mCoroutineContext, mDataInteractor) as T
            }
            modelClass.isAssignableFrom(FavouriteViewModel::class.java) -> {
                FavouriteViewModel(mCoroutineContext, mDataInteractor) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(mCoroutineContext, mDataInteractor) as T
            }

            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}