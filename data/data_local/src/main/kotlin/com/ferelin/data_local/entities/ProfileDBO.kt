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

@Entity(tableName = "companies_profile")
data class ProfileDBO(
    @PrimaryKey
    @ColumnInfo(name = "relation_company_id")
    val relationCompanyId: Int,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "web_url")
    val webUrl: String,

    @ColumnInfo(name = "industry")
    val industry: String,

    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "capitalization")
    val capitalization: String
)