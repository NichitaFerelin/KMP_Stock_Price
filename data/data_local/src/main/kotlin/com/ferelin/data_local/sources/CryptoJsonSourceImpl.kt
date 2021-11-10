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
import com.ferelin.data_local.mappers.CryptoMapper
import com.ferelin.data_local.pojo.CryptoPojo
import com.ferelin.domain.entities.Crypto
import com.ferelin.domain.sources.CryptoJsonSource
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import javax.inject.Inject

class CryptoJsonSourceImpl @Inject constructor(
    private val context: Context,
    private val cryptoMapper: CryptoMapper
) : CryptoJsonSource {

    companion object {
        private const val cryptoJsonFileName = "crypto.json"
    }

    private val moshi by lazy(LazyThreadSafetyMode.NONE) {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(): List<Crypto> =
        try {
            Timber.d("parse")

            val type = Types.newParameterizedType(List::class.java, CryptoPojo::class.java)

            val json = context.assets
                .open(cryptoJsonFileName)
                .bufferedReader()
                .use { it.readText() }

            val adapter = moshi.adapter<List<CryptoPojo>?>(type)
            val parsedItems = adapter.fromJson(json) ?: emptyList()

            Timber.d("parse result size = ${parsedItems.size}")

            List(parsedItems.size) { index ->
                cryptoMapper.map(parsedItems[index], index)
            }
        } catch (exception: JsonDataException) {
            Timber.d("parse exception $exception")
            emptyList()
        }
}