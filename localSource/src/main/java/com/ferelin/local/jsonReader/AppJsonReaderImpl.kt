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

package com.ferelin.local.jsonReader

import android.content.Context
import com.ferelin.local.database.Company
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppJsonReaderImpl @Inject constructor(
    private val mContext: Context
) : AppJsonReader {

    private val mMoshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private companion object {
        const val sCompaniesJsonFileName = "companies.json"
    }

    override suspend fun getCompaniesFromJson(): List<Company> {
        return readCompanies()
    }

    private fun readCompanies(): List<Company> {
        val json = mContext.assets
            .open(sCompaniesJsonFileName)
            .bufferedReader()
            .use { it.readText() }

        val type = Types.newParameterizedType(List::class.java, Company::class.java)
        val adapter: JsonAdapter<List<Company>> = mMoshi.adapter(type)

        return adapter.fromJson(json) ?: emptyList()
    }
}