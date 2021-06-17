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

package com.ferelin.stockprice.dataInteractor.helpers.favouriteCompaniesHelper

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.dataMediator.DataMediator
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteCompaniesHelperImpl @Inject constructor(
    private val mDataMediator: DataMediator,
    private val mSynchronizationManager: SynchronizationManager
) : FavouriteCompaniesHelper {

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onAddFavouriteCompany(adaptiveCompany).also { isAdded ->
            /**
             * The company may not be accepted to favourites.
             * If accepted -> then notify to sync manager
             */
            if (isAdded) {
                mSynchronizationManager.onCompanyAddedToLocal(adaptiveCompany)
            }
        }
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onRemoveFavouriteCompany(adaptiveCompany)
        mSynchronizationManager.onCompanyRemovedFromLocal(adaptiveCompany)
    }

    override suspend fun addCompanyToFavourite(symbol: String) {
        mDataMediator.getCompany(symbol)?.let { addCompanyToFavourite(it) }
    }

    override suspend fun removeCompanyFromFavourite(symbol: String) {
        mDataMediator.getCompany(symbol)?.let { removeCompanyFromFavourite(it) }
    }
}