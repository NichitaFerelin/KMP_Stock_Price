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

package com.ferelin.repository.adaptiveModels

data class AdaptiveCompany(
    val id: Int,

    var companyProfile: CompanyProfile,
    var companyDayData: StockPrice,
    var companyHistory: StockHistory,
    var companyNews: CompanyNews,
    var companyStyle: UiStockStyle,
    var isFavourite: Boolean = false,
    var favouriteOrderIndex: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompany) {
            companyProfile.symbol == other.companyProfile.symbol
        } else false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + companyProfile.name.hashCode()
        result = 31 * result + companyProfile.symbol.hashCode()
        result = 31 * result + companyDayData.currentPrice.hashCode()
        return result
    }

    override fun toString(): String {
        return companyProfile.symbol
    }
}