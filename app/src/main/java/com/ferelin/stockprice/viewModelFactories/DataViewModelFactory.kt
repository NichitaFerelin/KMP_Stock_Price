package com.ferelin.stockprice.viewModelFactories

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.bottomDrawerSection.login.LoginViewModel
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.MenuViewModel
import com.ferelin.stockprice.ui.previewSection.loading.LoadingViewModel
import com.ferelin.stockprice.ui.stocksSection.favourite.FavouriteViewModel
import com.ferelin.stockprice.ui.stocksSection.search.SearchViewModel
import com.ferelin.stockprice.ui.stocksSection.stocks.StocksViewModel
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerViewModel

@Suppress("UNCHECKED_CAST")
open class DataViewModelFactory(
    private val mCoroutineContext: CoroutineContextProvider,
    private val mDataInteractor: DataInteractor
) : ViewModelProvider.Factory {

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
            modelClass.isAssignableFrom(LoadingViewModel::class.java) -> {
                LoadingViewModel(mCoroutineContext, mDataInteractor) as T
            }
            modelClass.isAssignableFrom(StocksPagerViewModel::class.java) -> {
                StocksPagerViewModel(mCoroutineContext, mDataInteractor) as T
            }
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> {
                MenuViewModel(mCoroutineContext, mDataInteractor) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(mCoroutineContext, mDataInteractor) as T
            }

            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}