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

package com.ferelin.data_local.database

import androidx.room.*
import com.ferelin.data_local.entities.CompanyDBO
import com.ferelin.data_local.entities.CompanyWithStockPriceDBO

@Dao
interface CompaniesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CompanyDBO>)

    @Transaction
    @Query("SELECT * FROM `companies`")
    suspend fun getAll(): List<CompanyWithStockPriceDBO>

    @Transaction
    @Query("SELECT * FROM `companies` WHERE is_favourite = :condition")
    suspend fun getAllFavourites(condition: Boolean = true): List<CompanyWithStockPriceDBO>

    @Query(
        "UPDATE `companies` " +
                "SET is_favourite = :condition ,added_by_index = :param " +
                "WHERE NOT is_favourite = :condition"
    )
    suspend fun rollbackToDefault(condition: Boolean = false, param: Int = 0)

    @Query(
        "UPDATE `companies` " +
                "SET is_favourite = :isFavourite ,added_by_index = :addedByIndex " +
                "WHERE company_id = :companyId"
    )
    suspend fun updateIsFavourite(
        companyId: Int,
        isFavourite: Boolean,
        addedByIndex: Int
    )
}