package com.ferelin.local.json

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

import android.content.Context
import com.ferelin.local.models.Company
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class JsonAssetsReader(
    private val mContext: Context,
    private val mFileName: String,
) {
    private val mMoshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /*
    * Parsing companies data from json
    * */
    @Suppress("BlockingMethodInNonBlockingContext")
    fun readCompanies(): Flow<List<Company>> = flow {
        val json = mContext.assets.open(mFileName).bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, Company::class.java)
        val adapter: JsonAdapter<List<Company>> = mMoshi.adapter(type)
        val list = adapter.fromJson(json) ?: emptyList()
        emit(list)
    }
        .flowOn(Dispatchers.Default)
        .catch { emit(emptyList()) }
}