package com.ferelin.stockprice.ui.stocksSection.stocks

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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StocksViewModel : BaseStocksViewModel() {

    private val mEventError = MutableSharedFlow<String>()
    val eventError: SharedFlow<String>
        get() = mEventError

    val stateCompanies: Flow<DataNotificator.DataPrepared<List<AdaptiveCompany>>>
        get() = mDataInteractor.stateCompanies
            .filter { it is DataNotificator.DataPrepared }
            .take(1)
            .map { it as DataNotificator.DataPrepared }

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectSharedFavouritesLimitReached() }
            launch { collectSharedCompanyQuoteError() }
        }
    }

    private suspend fun collectSharedFavouritesLimitReached() {
        mDataInteractor.sharedFavouriteCompaniesLimitReached.collect { mEventError.emit(it) }
    }

    private suspend fun collectSharedCompanyQuoteError() {
        mDataInteractor.sharedLoadCompanyQuoteError.collect { mEventError.emit(it) }
    }
}