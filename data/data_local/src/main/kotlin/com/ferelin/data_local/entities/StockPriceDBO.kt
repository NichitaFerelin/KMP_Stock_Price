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
import com.ferelin.data_local.entities.StockPriceDBO.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class StockPriceDBO(
    @PrimaryKey
    @ColumnInfo(name = "relation_company_id")
    val relationCompanyId: Int,

    @ColumnInfo(name = "current_price")
    var currentPrice: Double,

    @ColumnInfo(name = "previous_close_price")
    var previousClosePrice: Double,

    @ColumnInfo(name = "open_price")
    var openPrice: Double,

    @ColumnInfo(name = "high_price")
    var highPrice: Double,

    @ColumnInfo(name = "low_price")
    var lowPrice: Double
) {
    companion object {
        const val TABLE_NAME = "companies_stock_price"
    }
}