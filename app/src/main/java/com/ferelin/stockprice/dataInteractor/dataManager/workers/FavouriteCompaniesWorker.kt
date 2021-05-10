package com.ferelin.stockprice.dataInteractor.dataManager.workers

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

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.parseDoubleFromStr
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [FavouriteCompaniesWorker] providing an ability to:
 *   - Observing [mStateFavouriteCompanies] to display a list of favourite companies.
 *   - Observing [mSharedFavouriteCompaniesUpdates] to add/remove item from list of favourite companies.
 *
 * Also [FavouriteCompaniesWorker] manually doing:
 *   - Subscribing favourite companies for live-time updates using [mRepositoryHelper].
 *   - Unsubscribing companies from live-time updates when it was removed from favourites using [mRepositoryHelper].
 *   - Control the limit of favourite companies @see [mCompaniesLimit] notifying with [mErrorsWorker].
 *   - Using [mLocalInteractorHelper] to data caching.
 *   - Using [mStylesProvider] to change some stock fields that will be affect on stock's appearance.
 */

@Singleton
class FavouriteCompaniesWorker @Inject constructor(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mErrorsWorker: ErrorsWorker
) {
    private var mFavouriteCompanies: ArrayList<AdaptiveCompany> = arrayListOf()

    private val mStateFavouriteCompanies =
        MutableStateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>(DataNotificator.Loading())
    val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mStateFavouriteCompanies

    private val mSharedFavouriteCompaniesUpdates =
        MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mSharedFavouriteCompaniesUpdates

    /*
    * To observe company by foreground service
    * */
    private val mStateCompanyForObserver = MutableStateFlow<AdaptiveCompany?>(null)
    val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mStateCompanyForObserver

    /*
    * Subscribing over 50 items to live-time updates exceeds the limit of
    * web socket => over-limit-companies will not receive updates (or all companies depending
    * the api mood)
    * */
    private val mCompaniesLimit = 50

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        mFavouriteCompanies = ArrayList(companies
            .filter { it.isFavourite }
            .sortedByDescending { it.favouriteOrderIndex }
        )
        subscribeCompaniesOnLiveTimeUpdates()
        mStateCompanyForObserver.value = mFavouriteCompanies.firstOrNull()
        mStateFavouriteCompanies.value = DataNotificator.DataPrepared(mFavouriteCompanies)
    }

    suspend fun onCompanyChanged(company: AdaptiveCompany) {
        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.ItemUpdatedCommon(company))
    }

    suspend fun addCompanyToFavourites(company: AdaptiveCompany): AdaptiveCompany? {
        if (isFavouritesCompaniesLimitExceeded()) {
            mErrorsWorker.onFavouriteCompaniesLimitReached()
            return null
        }

        applyChangesToAddedFavouriteCompany(company)
        subscribeCompanyOnLiveTimeUpdates(company)
        mFavouriteCompanies.add(0, company)
        mStateCompanyForObserver.value = company
        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.NewItemAdded(company))
        mLocalInteractorHelper.cacheCompany(company)
        return company
    }

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany): AdaptiveCompany {
        applyChangesToRemovedFavouriteCompany(company)
        mRepositoryHelper.unsubscribeItemFromLiveTimeUpdates(company.companyProfile.symbol)
        mFavouriteCompanies.remove(company)

        if (mStateCompanyForObserver.value == company) {
            mStateCompanyForObserver.value = mFavouriteCompanies.firstOrNull()
        }

        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.ItemRemoved(company))
        mLocalInteractorHelper.cacheCompany(company)
        return company
    }

    fun subscribeCompaniesOnLiveTimeUpdates() {
        mFavouriteCompanies.forEach { subscribeCompanyOnLiveTimeUpdates(it) }
    }

    private fun isFavouritesCompaniesLimitExceeded(): Boolean {
        return mFavouriteCompanies.size >= mCompaniesLimit
    }

    private fun subscribeCompanyOnLiveTimeUpdates(company: AdaptiveCompany) {
        mRepositoryHelper.subscribeItemToLiveTimeUpdates(
            company.companyProfile.symbol,
            parseDoubleFromStr(company.companyDayData.openPrice)
        )
    }

    private fun applyChangesToRemovedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = false
            companyStyle.favouriteDefaultIconResource =
                mStylesProvider.getDefaultIconDrawable(isFavourite)
            companyStyle.favouriteSingleIconResource =
                mStylesProvider.getSingleIconDrawable(isFavourite)
        }
    }

    private fun applyChangesToAddedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            companyStyle.favouriteDefaultIconResource =
                mStylesProvider.getDefaultIconDrawable(isFavourite)
            companyStyle.favouriteSingleIconResource =
                mStylesProvider.getSingleIconDrawable(isFavourite)

            val orderIndex = mFavouriteCompanies.firstOrNull()?.favouriteOrderIndex?.plus(1) ?: 0
            favouriteOrderIndex = orderIndex
        }
    }
}