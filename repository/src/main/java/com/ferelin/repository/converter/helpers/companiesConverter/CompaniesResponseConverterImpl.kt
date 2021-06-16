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

package com.ferelin.repository.converter.helpers.companiesConverter

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.converter.adapter.DataAdapter
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompaniesResponseConverterImpl @Inject constructor(
    private val mAdapter: DataAdapter
) : CompaniesResponseConverter {

    override fun convertCompaniesResponseForUi(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return if (response is CompaniesResponse.Success) {
            val preparedData = when (response.code) {
                Responses.LOADED_FROM_JSON -> response.companies.map {
                    mAdapter.toAdaptiveCompanyFromJson(it)
                }
                else -> response.companies.map { mAdapter.toAdaptiveCompany(it) }
            }
            RepositoryResponse.Success(data = preparedData)
        } else RepositoryResponse.Failed()
    }


    override fun convertCompanyForLocal(company: AdaptiveCompany): Company {
        return mAdapter.toDatabaseCompany(company)
    }
}