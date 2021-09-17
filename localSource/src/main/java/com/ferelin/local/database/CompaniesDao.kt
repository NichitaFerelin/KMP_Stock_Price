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

package com.ferelin.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Represents methods for interacting with companies database
 * */
@Dao
interface CompaniesDao {

    /**
     * Caches updated/new company to database
     *
     * @param company is an updated/new company that must be cached
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheCompany(company: Company)

    /**
     * Caches list of companies to database
     *
     * @param list is a list of companies that must be cached
     * */
    @Insert
    suspend fun cacheAllCompanies(list: List<Company>)

    /**
     * @return cached companies from database
     * */
    @Query("SELECT * FROM `stockprice.companies.db`")
    suspend fun getAllCompanies(): List<Company>
}