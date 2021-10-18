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

@Singleton
class CompaniesInteractorImpl @Inject constructor(
    private val companiesLocalRepo: CompaniesLocalRepo,
    private val companiesRemoteRepo: CompaniesRemoteRepo,
    private val profileRepo: ProfileRepo,
    private val companiesJsonSource: CompaniesJsonSource,
    private val livePriceSource: LivePriceSource,
    private val authenticationSource: AuthenticationSource,
    private val companiesSyncer: CompaniesSyncer,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) : CompaniesInteractor, CompaniesInternal {

    private var companiesState: LoadState<List<CompanyWithStockPrice>> = LoadState.None()
    private var favouriteCompaniesState: LoadState<List<CompanyWithStockPrice>> = LoadState.None()

    private val _companyWithStockPriceChanges = MutableSharedFlow<CompanyWithStockPrice>()
    override val companyWithStockPriceChanges: SharedFlow<CompanyWithStockPrice>
        get() = _companyWithStockPriceChanges.asSharedFlow()

    private val _favouriteCompaniesUpdates = MutableSharedFlow<CompanyWithStockPrice>()
    override val favouriteCompaniesUpdates: SharedFlow<CompanyWithStockPrice>
        get() = _favouriteCompaniesUpdates.asSharedFlow()

    override suspend fun getAll(): List<CompanyWithStockPrice> {
        companiesState.ifPrepared { preparedState ->
            return preparedState.data
        }

        companiesState = LoadState.Loading()
        favouriteCompaniesState = LoadState.Loading()

        return companiesLocalRepo.getAll()
            .ifEmpty {
                val fromJson = companiesJsonSource.parse()
                val companies = fromJson.first

                externalScope.launch(dispatchersProvider.IO) {
                    companiesLocalRepo.insertAll(companies)
                    profileRepo.insertAll(fromJson.second)
                }

                companies.map { CompanyWithStockPrice(it) }
            }
            .also { loadedCompanies ->
                companiesState = LoadState.Prepared(loadedCompanies)
                favouriteCompaniesState = LoadState.Prepared(
                    data = loadedCompanies.filter { it.company.isFavourite }
                )

                tryToSync()
            }
    }

    override suspend fun getAllFavourites(): List<CompanyWithStockPrice> {
        favouriteCompaniesState.ifPrepared { preparedState ->
            return preparedState.data
        }

        favouriteCompaniesState = LoadState.Loading()

        return companiesLocalRepo.getAllFavourites()
            .also { loadedCompanies ->
                val favouriteCompanies = loadedCompanies
                    .filter { it.company.isFavourite }
                    .onEach { livePriceSource.subscribeCompanyOnUpdates(it.company.ticker) }

                favouriteCompaniesState = LoadState.Prepared(favouriteCompanies)
            }
    }

    override suspend fun addCompanyToFavourites(companyId: Int) {
        findById(companyId)?.let { addCompanyToFavourites(it) }
    }

    override suspend fun addCompanyToFavourites(company: Company) {
        invokeWithStateHandleOnFavItem(company) { itemToUpdate, favouriteCompanies ->

            val orderIndex = favouriteCompanies
                .firstOrNull()
                ?.let { it.company.addedByIndex + 1 } ?: 0

            itemToUpdate.company.isFavourite = true
            itemToUpdate.company.addedByIndex = orderIndex

            favouriteCompanies.add(0, itemToUpdate)
            favouriteCompaniesState = LoadState.Prepared(favouriteCompanies)

            _favouriteCompaniesUpdates.emit(itemToUpdate)

            livePriceSource.subscribeCompanyOnUpdates(itemToUpdate.company.ticker)

            externalScope.launch(dispatchersProvider.IO) {
                launch {
                    companiesLocalRepo.updateIsFavourite(
                        itemToUpdate.company.id,
                        itemToUpdate.company.isFavourite,
                        itemToUpdate.company.addedByIndex
                    )
                }

                launch {
                    authenticationSource.getUserToken()?.let { userToken ->
                        companiesRemoteRepo.insertBy(
                            userToken,
                            itemToUpdate.company.id
                        )
                    }
                }
            }
        }
    }

    override suspend fun eraseCompanyFromFavourites(companyId: Int) {
        findById(companyId)?.let {
            eraseCompanyFromFavourites(it)
        }
    }

    override suspend fun eraseCompanyFromFavourites(company: Company) {
        invokeWithStateHandleOnFavItem(company) { itemToUpdate, favouriteCompanies ->

            itemToUpdate.company.isFavourite = false
            itemToUpdate.company.addedByIndex = 0

            favouriteCompanies.remove(itemToUpdate)
            favouriteCompaniesState = LoadState.Prepared(favouriteCompanies)

            _favouriteCompaniesUpdates.emit(itemToUpdate)

            livePriceSource.unsubscribeCompanyFromUpdates(itemToUpdate.company.ticker)

            externalScope.launch(dispatchersProvider.IO) {
                launch {
                    companiesLocalRepo.updateIsFavourite(
                        itemToUpdate.company.id,
                        itemToUpdate.company.isFavourite,
                        itemToUpdate.company.addedByIndex
                    )
                }
                launch {
                    authenticationSource.getUserToken()?.let { userToken ->
                        companiesRemoteRepo.eraseBy(
                            userToken,
                            itemToUpdate.company.id
                        )
                    }
                }
            }
        }
    }

    override suspend fun onStockPriceChanged(stockPrice: StockPrice) {
        updateCachedCompany(stockPrice.relationCompanyId) { itemToUpdate ->
            itemToUpdate.stockPrice = stockPrice
        }
    }

    override suspend fun onStockPriceChanged(liveTimePrice: LiveTimePrice) {
        // TODO always 0 id
        updateCachedCompany(liveTimePrice.relationCompanyId) { itemToUpdate ->
            itemToUpdate.stockPrice?.currentPrice = liveTimePrice.price
        }
    }

    override suspend fun eraseUserData() {
        invalidateUserData()
    }

    override suspend fun onNetworkAvailable() {
        tryToSync()
    }

    override suspend fun onNetworkLost() {
        companiesSyncer.invalidate()
    }

    override suspend fun onLogIn() {
        tryToSync()
    }

    override suspend fun onLogOut() {
        invalidateUserData()
        companiesSyncer.invalidate()
    }

    private suspend fun updateCachedCompany(
        companyId: Int,
        onUpdate: (CompanyWithStockPrice) -> Unit
    ) {
        companiesState.ifPrepared { preparedState ->

            val targetIndex = preparedState
                .data
                .binarySearchBy(companyId) { it.company.id }

            val companyWithStockPrice = preparedState.data[targetIndex]
            onUpdate.invoke(companyWithStockPrice)

            if (companyWithStockPrice.company.isFavourite) {
                favouriteCompaniesState.ifPrepared { favouriteCompaniesState ->

                    val targetIndexFav = favouriteCompaniesState
                        .data
                        .indexOfFirst { it.company.id == companyId }

                    onUpdate.invoke(favouriteCompaniesState.data[targetIndexFav])
                }
            }
            _companyWithStockPriceChanges.emit(companyWithStockPrice)
        }
    }

    private suspend fun tryToSync() {
        favouriteCompaniesState.ifPrepared { preparedState ->
            authenticationSource.getUserToken()?.let { userToken ->
                val favouriteIds = preparedState
                    .data
                    .map { it.company.id }

                val receivedCompaniesIds = companiesSyncer.initDataSync(userToken, favouriteIds)
                applyRemoteCompaniesIds(receivedCompaniesIds)
            }
        }
    }

    private suspend fun applyRemoteCompaniesIds(remoteIds: List<Int>) {
        companiesState.ifPrepared { preparedState ->
            val sourceCompanies = preparedState.data

            remoteIds.forEach { id ->
                val targetCompanyIndex = sourceCompanies.indexOfFirst { it.company.id == id }
                if (targetCompanyIndex != NULL_INDEX) {
                    addCompanyToFavourites(sourceCompanies[targetCompanyIndex].company)
                }
            }
        }
    }

    private suspend fun invokeWithStateHandleOnFavItem(
        company: Company,
        onAction: suspend (CompanyWithStockPrice, MutableList<CompanyWithStockPrice>) -> Unit
    ) {
        companiesState.ifPrepared { preparedCompaniesState ->
            favouriteCompaniesState.ifPrepared { preparedFavouritesState ->

                val targetCompanyIndex = preparedCompaniesState
                    .data
                    .binarySearch {
                        when {
                            company.id == it.company.id -> 0
                            company.id > it.company.id -> -1
                            else -> 1
                        }
                    }

                val targetCompany = preparedCompaniesState.data[targetCompanyIndex]
                val favouriteCompanies = preparedFavouritesState.data.toMutableList()
                onAction.invoke(targetCompany, favouriteCompanies)
            }
        }
    }

    private fun invalidateUserData() {
        externalScope.launch(dispatchersProvider.IO) {
            favouriteCompaniesState.ifPrepared { preparedState ->

                preparedState.data.forEach { companyWithStockPrice ->
                    eraseCompanyFromFavourites(companyWithStockPrice.company)
                }
            }
        }
    }

    private fun findById(companyId: Int): Company? {
        return companiesState.ifPrepared { preparedState ->
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