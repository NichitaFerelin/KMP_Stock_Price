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

import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.internals.CompaniesInternal
import com.ferelin.domain.repositories.ProfileRepo
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.CompaniesSource
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.domain.utils.ifPrepared
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NULL_INDEX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class CompaniesState {
    class Prepared(var companiesWithStockPrice: List<CompanyWithStockPrice>) : CompaniesState()
    object Loading : CompaniesState()
    object None : CompaniesState()
    object Error : CompaniesState()
}

@Singleton
class CompaniesInteractorImpl @Inject constructor(
    private val mCompaniesLocalRepo: CompaniesLocalRepo,
    private val mCompaniesRemoteRepo: CompaniesRemoteRepo,
    private val mProfileRepo: ProfileRepo,
    private val mCompaniesSource: CompaniesSource,
    private val mLivePriceSource: LivePriceSource,
    private val mAuthenticationSource: AuthenticationSource,
    private val mCompaniesSyncer: CompaniesSyncer,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : CompaniesInteractor, CompaniesInternal {

    private val mCompanyWithStockPriceChanged = MutableSharedFlow<CompanyWithStockPrice>()
    override val companyWithStockPriceChanges: SharedFlow<CompanyWithStockPrice>
        get() = mCompanyWithStockPriceChanged.asSharedFlow()

    private var mCompaniesState: CompaniesState = CompaniesState.None
    private var mFavouriteCompaniesState: CompaniesState = CompaniesState.None

    private val mFavouriteCompanyUpdates = MutableSharedFlow<Company>()

    override suspend fun getAll(): List<CompanyWithStockPrice> {
        if (mCompaniesState is CompaniesState.Prepared) {
            return (mCompaniesState as CompaniesState.Prepared).companiesWithStockPrice
        }

        mCompaniesState = CompaniesState.Loading
        mFavouriteCompaniesState = CompaniesState.Loading

        return mCompaniesLocalRepo.getAll()
            .ifEmpty {
                val fromJson = mCompaniesSource.getCompaniesWithProfileFromJson()
                val companies = fromJson.first

                mExternalScope.launch(mDispatchersProvider.IO) {
                    mCompaniesLocalRepo.cache(companies)
                    mProfileRepo.cacheProfiles(fromJson.second)
                }

                companies.map { CompanyWithStockPrice(it) }
            }
            .also { loadedCompanies ->
                mCompaniesState = CompaniesState.Prepared(loadedCompanies)
                mFavouriteCompaniesState = CompaniesState.Prepared(
                    companiesWithStockPrice = loadedCompanies.filter { it.company.isFavourite }
                )

                tryToSync()
            }
    }

    override suspend fun getAllFavourites(): List<CompanyWithStockPrice> {
        if (mFavouriteCompaniesState is CompaniesState.Prepared) {
            return (mFavouriteCompaniesState as CompaniesState.Prepared).companiesWithStockPrice
        }

        mFavouriteCompaniesState = CompaniesState.Loading

        return mCompaniesLocalRepo.getAllFavourites()
            .also { loadedCompanies ->
                val favouriteCompanies = loadedCompanies
                    .filter { it.company.isFavourite }
                    .onEach { mLivePriceSource.subscribeCompanyOnUpdates(it.company.ticker) }

                mFavouriteCompaniesState = CompaniesState.Prepared(favouriteCompanies)
            }
    }

    override suspend fun addCompanyToFavourites(company: Company) {
        if (mFavouriteCompaniesState !is CompaniesState.Prepared) {
            throw IllegalStateException(
                "Attempting to make 'Company' object favourite " +
                        "without first calling it from the database"
            )
        }

        val favouriteCompanies =
            (mFavouriteCompaniesState as CompaniesState.Prepared).companiesWithStockPrice
        val orderIndex = favouriteCompanies
            .firstOrNull()
            ?.let { it.company.addedByIndex + 1 } ?: 0

        company.isFavourite = true
        company.addedByIndex = orderIndex

        mFavouriteCompanyUpdates.emit(company)

        mLivePriceSource.subscribeCompanyOnUpdates(company.ticker)

        mExternalScope.launch(mDispatchersProvider.IO) {
            launch {
                mCompaniesLocalRepo.updateIsFavourite(
                    companyId = company.id,
                    isFavourite = company.isFavourite,
                    addedByIndex = company.addedByIndex
                )
            }

            launch {
                mAuthenticationSource.getUserToken()?.let { userToken ->
                    mCompaniesRemoteRepo.cacheCompanyIdToFavourites(
                        userToken,
                        company.id
                    )
                }
            }
        }
    }

    override suspend fun removeCompanyFromFavourites(company: Company) {
        company.isFavourite = false
        company.addedByIndex = 0

        mFavouriteCompanyUpdates.emit(company)

        mLivePriceSource.unsubscribeCompanyFromUpdates(company.ticker)

        mExternalScope.launch(mDispatchersProvider.IO) {
            launch {
                mCompaniesLocalRepo.updateIsFavourite(
                    companyId = company.id,
                    isFavourite = company.isFavourite,
                    addedByIndex = company.addedByIndex
                )
            }

            launch {
                mAuthenticationSource.getUserToken()?.let { userToken ->
                    mCompaniesRemoteRepo.eraseCompanyIdFromFavourites(
                        userToken,
                        company.id
                    )
                }
            }
        }
    }

    override fun observeFavouriteCompaniesUpdates(): SharedFlow<Company> {
        return mFavouriteCompanyUpdates.asSharedFlow()
    }

    override suspend fun onStockPriceChanged(stockPrice: StockPrice) {
        updateCachedCompany(stockPrice.id) { itemToUpdate ->
            itemToUpdate.stockPrice = stockPrice
        }
    }

    override suspend fun onStockPriceChanged(liveTimePrice: LiveTimePrice) {
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
        mFavouriteCompaniesState = CompaniesState.Prepared(emptyList())

        mExternalScope.launch(mDispatchersProvider.IO) {
            mCompaniesLocalRepo.setToDefault()
        }
    }

    private suspend fun updateCachedCompany(
        companyId: Int,
        onUpdate: (CompanyWithStockPrice) -> Unit
    ) {
        mCompaniesState.ifPrepared { companiesState ->

            val targetIndex = companiesState
                .companiesWithStockPrice
                .binarySearchBy(companyId) { it.company.id }

            val companyWithStockPrice = companiesState.companiesWithStockPrice[targetIndex]
            onUpdate.invoke(companyWithStockPrice)

            if (companyWithStockPrice.company.isFavourite) {
                mFavouriteCompaniesState.ifPrepared { favouriteCompaniesState ->

                    val targetIndexFav = favouriteCompaniesState
                        .companiesWithStockPrice
                        .indexOfFirst { it.company.id == companyId }

                    onUpdate.invoke(favouriteCompaniesState.companiesWithStockPrice[targetIndexFav])
                }
            }

            mCompanyWithStockPriceChanged.emit(companyWithStockPrice)
        }
    }

    private suspend fun tryToSync() {
        mFavouriteCompaniesState.let { favouriteCompaniesState ->
            if (favouriteCompaniesState !is CompaniesState.Prepared) {
                return
            }

            mAuthenticationSource.getUserToken()?.let { userToken ->
                val favouriteIds = favouriteCompaniesState
                    .companiesWithStockPrice
                    .map { it.company.id }

                val receivedCompaniesIds = mCompaniesSyncer.initDataSync(userToken, favouriteIds)
                applyRemoteCompaniesIds(receivedCompaniesIds)
            }
        }
    }

    private suspend fun applyRemoteCompaniesIds(remoteIds: List<Int>) {
        mCompaniesState.let { companiesState ->
            if (companiesState !is CompaniesState.Prepared) {
                return
            }

            val sourceCompanies = companiesState.companiesWithStockPrice
            remoteIds.forEach { id ->
                val targetCompanyIndex = sourceCompanies.indexOfFirst { it.company.id == id }
                if (targetCompanyIndex != NULL_INDEX) {
                    addCompanyToFavourites(sourceCompanies[targetCompanyIndex].company)
                }
            }
        }
    }
}