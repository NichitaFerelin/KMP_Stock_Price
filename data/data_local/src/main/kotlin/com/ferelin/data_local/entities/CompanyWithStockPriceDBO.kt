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

import androidx.room.Embedded
import androidx.room.Relation

data class CompanyWithStockPriceDBO(
    @Embedded
    val companyDBO: CompanyDBO,

    @Relation(
        parentColumn = CompanyDBO.ID_COLUMN,
        entityColumn = StockPriceDBO.ID_COLUMN
    )
    val stockPriceDBO: StockPriceDBO? = null
)