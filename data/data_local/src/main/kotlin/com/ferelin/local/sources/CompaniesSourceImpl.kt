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

package com.ferelin.local.sources

import android.content.Context
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.Profile
import com.ferelin.domain.sources.CompaniesSource
import com.ferelin.local.mappers.CompanyMapper
import com.ferelin.local.mappers.ProfileMapper
import com.ferelin.local.utils.CompanyPojo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CompaniesSourceImpl @Inject constructor(
    @Named("CompaniesJsonFileName") private val mCompaniesJsonFileName: String,
    private val mContext: Context,
    private val mCompanyMapper: CompanyMapper,
    private val mProfileMapper: ProfileMapper
) : CompaniesSource {

    private val mMoshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getCompaniesWithProfileFromJson(): Pair<List<Company>, List<Profile>> =
        try {
            Timber.d("get companies with profile from json")
            val type = Types.newParameterizedType(List::class.java, CompanyPojo::class.java)
            val json = mContext.assets
                .open(mCompaniesJsonFileName)
                .bufferedReader()
                .use { it.readText() }
            val adapter = mMoshi.adapter<List<CompanyPojo>?>(type)
            val parsedList = adapter.fromJson(json) ?: emptyList()

            Timber.d("json parsed companies size = ${parsedList.size}")

            val companies = List(parsedList.size) { index ->
                mCompanyMapper.map(index, parsedList[index])
            }

            val profiles = List(parsedList.size) { index ->
                mProfileMapper.map(index, parsedList[index])
            }

            Pair(companies, profiles)
        } catch (exception: JsonDataException) {
            Timber.d("json parsed exception $exception")
            Pair(emptyList(), emptyList())
        }
}