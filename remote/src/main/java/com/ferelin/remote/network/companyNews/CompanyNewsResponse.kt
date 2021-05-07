package com.ferelin.remote.network.companyNews

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

import com.squareup.moshi.Json

class CompanyNewsResponse(
    @Json(name = "datetime") val dateTime: Double,
    @Json(name = "headline") val headline: String,
    @Json(name = "id") val newsId: Double,
    @Json(name = "image") val previewImageUrl: String,
    @Json(name = "source") val newsSource: String,
    @Json(name = "summary") val newsSummary: String,
    @Json(name = "url") val newsBrowserUrl: String
)