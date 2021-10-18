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

package com.ferelin.local.reposirotires

import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
import com.ferelin.local.database.CompaniesDao
import com.ferelin.local.mappers.CompanyMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CompaniesRepoImpl @Inject constructor(
    private val companiesDao: CompaniesDao,
    private val companyMapper: CompanyMapper,
    private val dispatchersProvider: DispatchersProvider
) : CompaniesLocalRepo {

    override suspend fun insertAll(companies: List<Company>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (size = ${companies.size})")

            companiesDao.insertAll(
                list = companies.map(companyMapper::map)
            )
        }

    override suspend fun getAll(): List<CompanyWithStockPrice> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all")

            companiesDao
                .getAll()
                .map(companyMapper::map)
        }

    override suspend fun getAllFavourites(): List<CompanyWithStockPrice> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all favourites")

            companiesDao
                .getAllFavourites()
                .map(companyMapper::map)
        }

    override suspend fun rollbackToDefault() =
        withContext(dispatchersProvider.IO) {
            Timber.d("rollback to default")

            companiesDao.rollbackToDefault()
        }

    override suspend fun updateIsFavourite(
        companyId: Int,
        isFavourite: Boolean,
        addedByIndex: Int
    ) = withContext(dispatchersProvider.IO) {
        Timber.d(
            "update is favourite (companyId = $companyId," +
                    " isFavourite = $isFavourite, addedByIndex = $addedByIndex)"
        )

        companiesDao.updateIsFavourite(companyId, isFavourite, addedByIndex)
    }
}