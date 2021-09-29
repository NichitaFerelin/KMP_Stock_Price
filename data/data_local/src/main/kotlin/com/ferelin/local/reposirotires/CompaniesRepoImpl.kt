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
import javax.inject.Inject

class CompaniesRepoImpl @Inject constructor(
    private val mCompaniesDao: CompaniesDao,
    private val mCompanyMapper: CompanyMapper,
    private val mDispatchersProvider: DispatchersProvider
) : CompaniesLocalRepo {

    override suspend fun getAll(): List<Company> =
        withContext(mDispatchersProvider.IO) {
            return@withContext mCompaniesDao
                .getAllCompanies()
                .map(mCompanyMapper::map)
        }

    override suspend fun getAllFavourites(): List<Company> =
        withContext(mDispatchersProvider.IO) {
            return@withContext mCompaniesDao
                .getAllFavourites()
                .map(mCompanyMapper::map)
        }


    override suspend fun cache(companies: List<Company>) =
        withContext(mDispatchersProvider.IO) {
            mCompaniesDao.insertAllCompanies(
                list = companies.map(mCompanyMapper::map)
            )
        }

    override suspend fun setToDefault() =
        withContext(mDispatchersProvider.IO) {
            mCompaniesDao.setToDefault()
        }

    override suspend fun updateIsFavourite(
        companyId: Int,
        isFavourite: Boolean,
        addedByIndex: Int
    ) = withContext(mDispatchersProvider.IO) {
        mCompaniesDao.updateIsFavourite(companyId, isFavourite, addedByIndex)
    }

    override suspend fun getCompaniesWithStocksPrice(): List<CompanyWithStockPrice> =
        withContext(mDispatchersProvider.IO) {
            mCompaniesDao
                .getCompaniesWithStocksPrice()
                .map(mCompanyMapper::map)
        }
}