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
 * Represents api that returns stock price history
 * */
interface PastPricesApi {

    /**
     * Requests stock price history data
     *
     * @param companyTicker is a company symbol for which stock history is need
     * @param token is an api token required to access the server
     * @param from represents time-millis string starting from which need to return stock history
     * @param to represents time-millis string ending to which need to return stock history
     * @param resolution is type in which need to return data. By day / week / month, etc.
     * @return server response as [PastPricesResponse] object
     * */
    @GET("stock/candle")
    fun getPastPrices(
        @Query("symbol") companyTicker: String,
        @Query("token") token: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("resolution") resolution: String,
    ): Call<PastPricesResponse>
}

data class PastPricesResponse(
    @Json(name = "o") val openPrices: List<Double>,
    @Json(name = "h") val highPrices: List<Double>,
    @Json(name = "l") val lowPrices: List<Double>,
    @Json(name = "c") val closePrices: List<Double>,
    @Json(name = "v") val volumeData: List<Double>,
    @Json(name = "t") val timestamps: List<Long>,
    @Json(name = "s") val responseStatus: String
)