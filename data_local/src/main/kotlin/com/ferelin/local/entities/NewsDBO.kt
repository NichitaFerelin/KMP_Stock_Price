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

package com.ferelin.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "companies_news")
class NewsDBO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @Relation(
        parentColumn = CompanyDBO.ID_COLUMN,
        entityColumn = "relation_id"
    )
    @ColumnInfo(name = "relation_id")
    var relationId: Int,

    @ColumnInfo(name = "cloud_id")
    val cloudId: String,

    @ColumnInfo(name = "headline")
    val headline: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "preview_image_url")
    val previewImageUrl: String,

    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "source_url")
    val sourceUrl: String,

    @ColumnInfo(name = "summary")
    val summary: String
)