package com.ferelin.stockprice.ui.aboutSection.aboutSection

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
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class AboutPagerViewModel(val selectedCompany: AdaptiveCompany) : BaseViewModel() {

    val eventOnDataChanged: Flow<DataNotificator<AdaptiveCompany>>
        get() = mDataInteractor.sharedCompaniesUpdates.filter { filterUpdate(it) }

    val companySymbol: String
        get() = selectedCompany.companyProfile.symbol

    val companyName: String
        get() = selectedCompany.companyProfile.name

    val companyFavouriteIconResource: Int
        get() = selectedCompany.companyStyle.favouriteForegroundIconResource

    var selectedTabPagePosition: Int = 0

    override fun initObserversBlock() {
        // Do nothing
    }

    fun onFavouriteIconClicked() {
        viewModelScope.launch(mCoroutineContext.IO) {
            when (selectedCompany.isFavourite) {
                true -> mDataInteractor.removeCompanyFromFavourites(selectedCompany)
                false -> mDataInteractor.addCompanyToFavourites(selectedCompany)
            }
        }
    }

    private fun filterUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return notificator is DataNotificator.ItemUpdatedCommon
                && notificator.data != null
                && selectedCompany.companyProfile.symbol == notificator.data.companyProfile.symbol
    }
}