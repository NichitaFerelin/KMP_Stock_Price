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

package com.ferelin.data_local.sources

import android.content.Context
import com.ferelin.data_local.mappers.CompanyMapper
import com.ferelin.data_local.mappers.ProfileMapper
import com.ferelin.data_local.utils.CompanyPojo
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.Profile
import com.ferelin.domain.sources.CompaniesJsonSource
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import javax.inject.Inject

class CompaniesJsonSourceImpl @Inject constructor(
    private val context: Context,
    private val companyMapper: CompanyMapper,
    private val profileMapper: ProfileMapper
) : CompaniesJsonSource {

    companion object {
        private const val companiesJsonFileName = "companies.json"
    }

    private val moshi by lazy(LazyThreadSafetyMode.NONE) {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(): Pair<List<Company>, List<Profile>> =
        try {
            Timber.d("parse")

            val type = Types.newParameterizedType(List::class.java, CompanyPojo::class.java)

            val json = context.assets
                .open(companiesJsonFileName)
                .bufferedReader()
                .use { it.readText() }

            val adapter = moshi.adapter<List<CompanyPojo>?>(type)
            val parsedItems = adapter.fromJson(json) ?: emptyList()

            Timber.d("parse result size = ${parsedItems.size}")

            val companies = List(parsedItems.size) { index ->
                companyMapper.map(index, parsedItems[index])
            }
            val profiles = List(parsedItems.size) { index ->
                profileMapper.map(index, parsedItems[index])
            }

            Pair(companies, profiles)
        } catch (exception: JsonDataException) {
            Timber.d("parse exception $exception")
            Pair(emptyList(), emptyList())
        }
}