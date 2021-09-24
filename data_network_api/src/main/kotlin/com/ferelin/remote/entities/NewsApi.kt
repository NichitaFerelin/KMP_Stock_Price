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

package com.ferelin.remote.entities

import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Represents api that returns company news
 * */
interface NewsApi {

    /**
     * Requests company news data
     *
     * @param symbol is a company symbol for which news are need
     * @param token is an api token required to access the server
     * @param from represents time-millis string starting from which need to return news
     * @param to represents time-millis string ending to which need to return news
     * @return server response as [NewsResponse] object
     * */
    @GET("company-news")
    fun getNews(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<List<NewsResponse>>
}

data class NewsResponse(
    @Json(name = "datetime") val dateTime: Double,
    @Json(name = "headline") val headline: String,
    @Json(name = "id") val id: Double,
    @Json(name = "image") val previewImageUrl: String,
    @Json(name = "source") val source: String,
    @Json(name = "url") val sourceUrl: String,
    @Json(name = "summary") val summary: String
)