package com.ferelin.repository.adaptiveModels

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

data class AdaptiveCompany(
    val id: Int,

    var companyProfile: AdaptiveCompanyProfile,
    var companyDayData: AdaptiveCompanyDayData,
    var companyHistory: AdaptiveCompanyHistory,
    var companyNews: AdaptiveCompanyNews,
    var companyStyle: AdaptiveCompanyStyle,
    var isFavourite: Boolean = false,
    var favouriteOrderIndex: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompany) {
            companyProfile.name == other.companyProfile.name &&
                    companyProfile.symbol == other.companyProfile.symbol &&
                    companyDayData.currentPrice == other.companyDayData.currentPrice
        } else false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + companyProfile.hashCode()
        result = 31 * result + companyDayData.hashCode()
        result = 31 * result + companyHistory.hashCode()
        result = 31 * result + companyNews.hashCode()
        result = 31 * result + companyStyle.hashCode()
        result = 31 * result + isFavourite.hashCode()
        return result
    }
}