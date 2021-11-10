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

package com.ferelin.data_network_api.entities

import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoPriceApi {

    @GET("currencies/ticker")
    fun load(
        @Query("ids") cryptoNames: String,
        @Query("key") token: String
    ): Call<List<CryptoPriceResponse>>
}

data class CryptoPriceResponse(
    @Json(name = "symbol") val relationSymbol: String,
    @Json(name = "price") val price: String,
    @Json(name = "price_timestamp") val priceTimestamp: String,
    @Json(name = "high") val highPrice: String,
    @Json(name = "high_timestamp") val highPriceTimestamp: String,
    @Json(name = "7d") val priceChangeInfo: PriceChangeInfo
)

data class PriceChangeInfo(
    @Json(name = "price_change") val priceChange: String,
    @Json(name = "price_change_pct") val priceChangePercents: String
)