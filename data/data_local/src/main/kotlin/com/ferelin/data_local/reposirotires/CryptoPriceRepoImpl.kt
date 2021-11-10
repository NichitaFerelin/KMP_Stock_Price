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

package com.ferelin.data_local.reposirotires

import com.ferelin.data_local.database.CryptoPriceDao
import com.ferelin.data_local.mappers.CryptoPriceMapper
import com.ferelin.domain.entities.CryptoPrice
import com.ferelin.domain.entities.CryptoWithPrice
import com.ferelin.domain.repositories.CryptoPriceRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CryptoPriceRepoImpl @Inject constructor(
    private val cryptoPriceDao: CryptoPriceDao,
    private val cryptoPriceMapper: CryptoPriceMapper,
    private val dispatchersProvider: DispatchersProvider
) : CryptoPriceRepo {

    override suspend fun insertAll(cryptosPrice: List<CryptoPrice>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (size = ${cryptosPrice.size})")

            cryptoPriceDao.insertAll(
                cryptoPrices = cryptosPrice.map(cryptoPriceMapper::map)
            )
        }

    override suspend fun getAll(): List<CryptoWithPrice> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all")

            cryptoPriceDao
                .getAll()
                .map(cryptoPriceMapper::map)
        }
}