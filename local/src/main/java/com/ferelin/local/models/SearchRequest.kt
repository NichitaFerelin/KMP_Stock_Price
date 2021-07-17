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

package com.ferelin.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.local.databases.searchRequestsDb.SearchRequestsDatabase

@Entity(tableName = SearchRequestsDatabase.DB_NAME)
class SearchRequest(
    @PrimaryKey
    val id: Int,
    val searchRequest: String
)