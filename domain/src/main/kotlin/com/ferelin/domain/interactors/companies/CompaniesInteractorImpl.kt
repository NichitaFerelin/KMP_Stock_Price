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
import com.ferelin.domain.internals.CompaniesInternal
import com.ferelin.domain.internals.LiveTimePriceInternal
import com.ferelin.domain.repositories.ProfileRepo
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.CompaniesSource
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.domain.utils.NULL_INDEX
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class CompaniesState {
    class Prepared(val companies: List<Company>) : CompaniesState()
    object Loading : CompaniesState()
    object None : CompaniesState()
    object Error : CompaniesState()
}

@Singleton
class CompaniesInteractorImpl @Inject constructor(
    private val mCompaniesLocalRepo: CompaniesLocalRepo,
    private val mCompaniesRemoteRepo: CompaniesRemoteRepo,
    private val mCompaniesSource: CompaniesSource,
    private val mCompaniesSyncer: CompaniesSyncer,
    private val mProfileRepo: ProfileRepo,
    private val mAuthenticationSource: AuthenticationSource,
    private val mLiveTimePriceInternal: LiveTimePriceInternal,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : CompaniesInteractor, CompaniesInternal {

    private var mCompaniesState: CompaniesState = CompaniesState.None
    private var mFavouriteCompaniesState: CompaniesState = CompaniesState.None

    private var mCompaniesWithStockPrice: List<CompanyWithStockPrice> = emptyList()

    private val mFavouriteCompanyUpdates = MutableSharedFlow<Company>()

    override suspend fun getAllCompanies(): List<Company> {
        if (mCompaniesState is CompaniesState.Prepared) {
            return (mCompaniesState as CompaniesState.Prepared).companies
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
                companies
            }
            .also { loadedCompanies ->
                mCompaniesState = CompaniesState.Prepared(loadedCompanies)
                mFavouriteCompaniesState = CompaniesState.Prepared(
                    companies = loadedCompanies.filter { it.isFavourite }
                )

                tryToSync()
            }
    }

    override suspend fun getAllFavouriteCompanies(): List<Company> {
        if (mFavouriteCompaniesState is CompaniesState.Prepared) {
            return (mFavouriteCompaniesState as CompaniesState.Prepared).companies
        }

        mFavouriteCompaniesState = CompaniesState.Loading

        return mCompaniesLocalRepo.getAllFavourites()
            .also { loadedCompanies ->
                val favouriteCompanies = loadedCompanies
                    .filter { it.isFavourite }
                    .onEach { mLiveTimePriceInternal.subscribeCompanyOnUpdates(it.ticker) }

                mFavouriteCompaniesState = CompaniesState.Prepared(favouriteCompanies)
            }
    }

    override suspend fun getCompaniesWithStocksPrice(): List<CompanyWithStockPrice> {
        if (mCompaniesWithStockPrice.isNotEmpty()) {
            return mCompaniesWithStockPrice
        }

        val dbCompaniesWithStockPrice = mCompaniesLocalRepo.getCompaniesWithStocksPrice()
        mCompaniesWithStockPrice = dbCompaniesWithStockPrice
        return mCompaniesWithStockPrice
    }

    override suspend fun addCompanyToFavourites(company: Company) {
        if (mFavouriteCompaniesState !is CompaniesState.Prepared) {
            throw IllegalStateException(
                "Attempting to make 'Company' object favourite " +
                        "without first calling it from the database"
            )
        }

        val favouriteCompanies = (mFavouriteCompaniesState as CompaniesState.Prepared).companies
        val orderIndex = favouriteCompanies.firstOrNull()?.addedByIndex?.plus(1) ?: 0

        company.isFavourite = true
        company.addedByIndex = orderIndex

        mFavouriteCompanyUpdates.emit(company)

        mLiveTimePriceInternal.subscribeCompanyOnUpdates(company.ticker)

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

        mLiveTimePriceInternal.unsubscribeCompanyFromUpdates(company.ticker)

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

    private suspend fun tryToSync() {
        mFavouriteCompaniesState.let { favouriteCompaniesState ->
            if (favouriteCompaniesState !is CompaniesState.Prepared) {
                return
            }

            mAuthenticationSource.getUserToken()?.let { userToken ->
                val favouriteIds = favouriteCompaniesState
                    .companies
                    .map(Company::id)

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

            val sourceCompanies = companiesState.companies
            remoteIds.forEach { id ->
                val targetCompanyIndex = sourceCompanies.indexOfFirst { it.id == id }
                if (targetCompanyIndex != NULL_INDEX) {
                    addCompanyToFavourites(sourceCompanies[targetCompanyIndex])
                }
            }
        }
    }
}