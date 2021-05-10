package com.ferelin.local.database

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

import com.ferelin.local.models.Company
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CompaniesManager @Inject constructor(
    companiesDatabase: CompaniesDatabase
) : CompaniesManagerHelper {

    private val mCompaniesDao = companiesDatabase.companiesDao()

    override fun insertCompany(company: Company) {
        mCompaniesDao.insert(company)
    }

    override fun insertAllCompanies(list: List<Company>) {
        mCompaniesDao.insertAll(list)
    }

    override fun updateCompany(company: Company) {
        mCompaniesDao.update(company)
    }

    override fun getAllCompanies(): Flow<List<Company>> {
        return mCompaniesDao.getAllCompanies()
    }

    override fun deleteCompany(company: Company) {
        mCompaniesDao.delete(company)
    }
}


