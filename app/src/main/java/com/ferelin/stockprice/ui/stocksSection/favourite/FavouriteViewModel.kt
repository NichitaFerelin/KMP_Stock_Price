package com.ferelin.stockprice.ui.stocksSection.favourite

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

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel : BaseStocksViewModel() {

    private val mEventOnNewItem = MutableSharedFlow<Unit>()
    val eventOnNewItem: SharedFlow<Unit>
        get() = mEventOnNewItem

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateFavouriteCompanies() }
            launch { collectSharedFavouriteCompaniesUpdates() }
        }
    }

    private suspend fun collectStateFavouriteCompanies() {
        mDataInteractor.stateFavouriteCompanies
            .filter { it is DataNotificator.DataPrepared }
            .take(1)
            .collect {
                withContext(mCoroutineContext.Main) {
                    onFavouriteCompaniesPrepared(it)
                }
            }
    }

    private suspend fun collectSharedFavouriteCompaniesUpdates() {
        mDataInteractor.sharedFavouriteCompaniesUpdates
            .filter { it is DataNotificator.NewItemAdded || it is DataNotificator.ItemRemoved }
            .collect { onFavouriteCompanyUpdateShared(it) }
    }

    private fun onFavouriteCompaniesPrepared(notificator: DataNotificator<List<AdaptiveCompany>>) {
        mStocksRecyclerAdapter.setCompanies(ArrayList(notificator.data!!))
    }

    /**
     * Is important to modify adapter items based on view model lifecycle but not on view.
     * */
    private fun onFavouriteCompanyUpdateShared(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            notificator.data?.let {
                when (notificator) {
                    is DataNotificator.NewItemAdded -> {
                        withContext(mCoroutineContext.Main) {
                            mStocksRecyclerAdapter.addCompany(notificator.data)
                            mEventOnNewItem.emit(Unit)
                        }
                    }
                    is DataNotificator.ItemRemoved -> {
                        val index = mStocksRecyclerAdapter.companies.indexOf(notificator.data)
                        if (index != NULL_INDEX) {
                            withContext(mCoroutineContext.Main) {
                                mStocksRecyclerAdapter.removeCompany(index)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}