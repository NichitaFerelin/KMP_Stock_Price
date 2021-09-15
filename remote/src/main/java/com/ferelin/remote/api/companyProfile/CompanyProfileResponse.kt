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

package com.ferelin.remote.api.companyProfile

import com.squareup.moshi.Json

class CompanyProfileResponse(
    @Json(name = "name") val name: String,
    @Json(name = "logo") val logoUrl: String,
    @Json(name = "country") val country: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "weburl") val webUrl: String,
    @Json(name = "finnhubIndustry") val industry: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "marketCapitalization") val capitalization: Double
)
