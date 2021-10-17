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

package com.ferelin.domain.interactors.companies

import android.util.Log
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.internals.CompaniesInternal
import com.ferelin.domain.repositories.ProfileRepo
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.CompaniesJsonSource
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.ferelin.shared.NULL_INDEX
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

typealias Companies = List<CompanyWithStockPrice>

@Singleton
class CompaniesInteractorImpl @Inject constructor(
    private val mCompaniesLocalRepo: CompaniesLocalRepo,
    private val mCompaniesRemoteRepo: CompaniesRemoteRepo,
    private val mProfileRepo: ProfileRepo,
    private val mCompaniesJsonSource: CompaniesJsonSource,
    private val mLivePriceSource: LivePriceSource,
    private val mAuthenticationSource: AuthenticationSource,
    private val mCompaniesSyncer: CompaniesSyncer,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : CompaniesInteractor, CompaniesInternal {

    private var mCompaniesState: LoadState<Companies> = LoadState.None()
    private var mFavouriteCompaniesState: LoadState<Companies> = LoadState.None()

    private val mCompanyWithStockPriceChanges = MutableSharedFlow<CompanyWithStockPrice>()
    override val companyWithStockPriceChanges: SharedFlow<CompanyWithStockPrice>
        get() = mCompanyWithStockPriceChanges.asSharedFlow()

    private val mFavouriteCompanyUpdates = MutableSharedFlow<CompanyWithStockPrice>()
    override val favouriteCompaniesUpdated: SharedFlow<CompanyWithStockPrice>
        get() = mFavouriteCompanyUpdates.asSharedFlow()

    override suspend fun getAll(): List<CompanyWithStockPrice> {
        mCompaniesState.ifPrepared { preparedState ->
            return preparedState.data
        }

        mCompaniesState = LoadState.Loading()
        mFavouriteCompaniesState = LoadState.Loading()

        return mCompaniesLocalRepo.getAll()
            .ifEmpty {
                val fromJson = mCompaniesJsonSource.getCompaniesWithProfileFromJson()
                val companies = fromJson.first

                mExternalScope.launch(mDispatchersProvider.IO) {
                    mCompaniesLocalRepo.cache(companies)
                    mProfileRepo.cacheProfiles(fromJson.second)
                }

                companies.map { CompanyWithStockPrice(it) }
            }
            .also { loadedCompanies ->
                mCompaniesState = LoadState.Prepared(loadedCompanies)
                mFavouriteCompaniesState = LoadState.Prepared(
                    data = loadedCompanies.filter { it.company.isFavourite }
                )

                tryToSync()
            }
    }

    override suspend fun getAllFavourites(): List<CompanyWithStockPrice> {
        mFavouriteCompaniesState.ifPrepared { preparedState ->
            return preparedState.data
        }

        mFavouriteCompaniesState = LoadState.Loading()

        return mCompaniesLocalRepo.getAllFavourites()
            .also { loadedCompanies ->
                val favouriteCompanies = loadedCompanies
                    .filter { it.company.isFavourite }
                    .onEach { mLivePriceSource.subscribeCompanyOnUpdates(it.company.ticker) }

                mFavouriteCompaniesState = LoadState.Prepared(favouriteCompanies)
            }
    }

    override suspend fun addCompanyToFavourites(companyId: Int) {
        findById(companyId)?.let { addCompanyToFavourites(it) }
    }

    override suspend fun removeCompanyFromFavourites(
        companyId: Int,
        includingRemoteSource: Boolean
    ) {
        findById(companyId)?.let {
            removeCompanyFromFavourites(it, includingRemoteSource)
        }
    }

    override suspend fun clearUserData() {
        invalidateUserData(true)
    }

    override suspend fun addCompanyToFavourites(company: Company) {
        invokeWithStateHandleOnFavItem(company) { itemToUpdate, favouriteCompanies ->

            val orderIndex = favouriteCompanies
                .firstOrNull()
                ?.let { it.company.addedByIndex + 1 } ?: 0

            itemToUpdate.company.isFavourite = true
            itemToUpdate.company.addedByIndex = orderIndex

            favouriteCompanies.add(0, itemToUpdate)
            mFavouriteCompaniesState = LoadState.Prepared(favouriteCompanies)

            mFavouriteCompanyUpdates.emit(itemToUpdate)

            mLivePriceSource.subscribeCompanyOnUpdates(itemToUpdate.company.ticker)

            mExternalScope.launch(mDispatchersProvider.IO) {
                launch {
                    mCompaniesLocalRepo.updateIsFavourite(
                        companyId = itemToUpdate.company.id,
                        isFavourite = itemToUpdate.company.isFavourite,
                        addedByIndex = itemToUpdate.company.addedByIndex
                    )
                }

                launch {
                    mAuthenticationSource.getUserToken()?.let { userToken ->
                        mCompaniesRemoteRepo.cacheCompanyIdToFavourites(
                            userToken,
                            itemToUpdate.company.id
                        )
                    }
                }
            }
        }
    }

    override suspend fun removeCompanyFromFavourites(
        company: Company,
        includingRemoteSource: Boolean
    ) {
        invokeWithStateHandleOnFavItem(company) { itemToUpdate, favouriteCompanies ->

            itemToUpdate.company.isFavourite = false
            itemToUpdate.company.addedByIndex = 0

            favouriteCompanies.remove(itemToUpdate)
            mFavouriteCompaniesState = LoadState.Prepared(favouriteCompanies)

            mFavouriteCompanyUpdates.emit(itemToUpdate)

            mLivePriceSource.unsubscribeCompanyFromUpdates(itemToUpdate.company.ticker)

            mExternalScope.launch(mDispatchersProvider.IO) {
                launch {
                    mCompaniesLocalRepo.updateIsFavourite(
                        itemToUpdate.company.id,
                        itemToUpdate.company.isFavourite,
                        itemToUpdate.company.addedByIndex
                    )
                }
                if (includingRemoteSource) {
                    launch {
                        mAuthenticationSource.getUserToken()?.let { userToken ->
                            mCompaniesRemoteRepo.eraseCompanyIdFromFavourites(
                                userToken,
                                itemToUpdate.company.id
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun onStockPriceChanged(stockPrice: StockPrice) {
        updateCachedCompany(stockPrice.id) { itemToUpdate ->
            itemToUpdate.stockPrice = stockPrice
        }
    }

    override suspend fun onStockPriceChanged(liveTimePrice: LiveTimePrice) {
        // TODO always 0 id
        updateCachedCompany(liveTimePrice.companyId) { itemToUpdate ->
            itemToUpdate.stockPrice?.currentPrice = liveTimePrice.price
        }
    }

    override suspend fun onNetworkAvailable() {
        tryToSync()
    }

    override suspend fun onNetworkLost() {
        mCompaniesSyncer.invalidate()
    }

    override suspend fun onLogIn() {
        tryToSync()
    }

    override suspend fun onLogOut() {
        invalidateUserData(false)
        mCompaniesSyncer.invalidate()
    }

    private suspend fun updateCachedCompany(
        companyId: Int,
        onUpdate: (CompanyWithStockPrice) -> Unit
    ) {
        mCompaniesState.ifPrepared { preparedState ->

            val targetIndex = preparedState
                .data
                .binarySearchBy(companyId) { it.company.id }

            val companyWithStockPrice = preparedState.data[targetIndex]
            onUpdate.invoke(companyWithStockPrice)

            if (companyWithStockPrice.company.isFavourite) {
                mFavouriteCompaniesState.ifPrepared { favouriteCompaniesState ->

                    val targetIndexFav = favouriteCompaniesState
                        .data
                        .indexOfFirst { it.company.id == companyId }

                    onUpdate.invoke(favouriteCompaniesState.data[targetIndexFav])
                }
            }

            mCompanyWithStockPriceChanges.emit(companyWithStockPrice)
        }
    }

    private suspend fun tryToSync() {
        mFavouriteCompaniesState.ifPrepared { preparedState ->
            mAuthenticationSource.getUserToken()?.let { userToken ->
                val favouriteIds = preparedState
                    .data
                    .map { it.company.id }

                val receivedCompaniesIds = mCompaniesSyncer.initDataSync(userToken, favouriteIds)
                applyRemoteCompaniesIds(receivedCompaniesIds)
            }
        }
    }

    private suspend fun applyRemoteCompaniesIds(remoteIds: List<Int>) {
        mCompaniesState.ifPrepared { preparedState ->
            Log.d("TEST", "got size: ${remoteIds.size}")
            val sourceCompanies = preparedState.data
            remoteIds.forEach { id ->
                val targetCompanyIndex = sourceCompanies.indexOfFirst { it.company.id == id }
                if (targetCompanyIndex != NULL_INDEX) {
                    Log.d("TEST", "add to favourites with index $targetCompanyIndex")
                    addCompanyToFavourites(sourceCompanies[targetCompanyIndex].company)
                }
            }
        }
    }

    private suspend fun invokeWithStateHandleOnFavItem(
        company: Company,
        onAction: suspend (CompanyWithStockPrice, MutableList<CompanyWithStockPrice>) -> Unit
    ) {
        if (mCompaniesState !is LoadState.Prepared
            || mFavouriteCompaniesState !is LoadState.Prepared
        ) {
            throw IllegalStateException(
                "Attempting to make 'Company' object favourite " +
                        "without first calling it from the database"
            )
        }

        val targetCompanyIndex = (mCompaniesState as LoadState.Prepared)
            .data
            .binarySearch {
                when {
                    company.id == it.company.id -> 0
                    company.id > it.company.id -> -1
                    else -> 1
                }
            }

        val targetCompany = (mCompaniesState as LoadState.Prepared).data[targetCompanyIndex]

        val favouriteCompanies = (mFavouriteCompaniesState as LoadState.Prepared)
            .data
            .toMutableList()

        onAction.invoke(targetCompany, favouriteCompanies)
    }

    private fun invalidateUserData(includingRemoteSource: Boolean) {
        mExternalScope.launch(mDispatchersProvider.IO) {
            mFavouriteCompaniesState.ifPrepared { preparedState ->
                preparedState.data.forEach { companyWithStockPrice ->
                    removeCompanyFromFavourites(
                        companyWithStockPrice.company,
                        includingRemoteSource
                    )
                }
            }
        }
    }

    private fun findById(companyId: Int): Company? {
        return mCompaniesState.ifPrepared { preparedState ->
            val targetIndex = preparedState
                .data
                .binarySearch { companyWithStockPrice ->
                    when {
                        companyId > companyWithStockPrice.company.id -> -1
                        companyId < companyWithStockPrice.company.id -> 1
                        else -> 0
                    }
                }
            preparedState.data[targetIndex].company
        }
    }
}