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

package com.ferelin.data_local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.data_local.entities.CryptoPriceDBO.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CryptoPriceDBO(
    @PrimaryKey
    @ColumnInfo(name = ID_COLUMN)
    val relationCryptoId: Int = 0,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "price_timestamp")
    val priceTimestamp: String,

    @ColumnInfo(name = "high_price")
    val highPrice: Double,

    @ColumnInfo(name = "high_price_timestamp")
    val highPriceTimestamp: String,

    @ColumnInfo(name = "price_change")
    val priceChange: Double,

    @ColumnInfo(name = "price_change_percents")
    val priceChangePercents: Double
) {
    companion object {
        const val TABLE_NAME = "crypto_prices"
        const val ID_COLUMN = "relation_crypto_id"
    }
}