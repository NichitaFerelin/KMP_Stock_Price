package com.ferelin.repository.adaptiveModels

data class AdaptiveCompany(
    val id: Int,

    var companyProfile: AdaptiveCompanyProfile,
    var companyDayData: AdaptiveCompanyDayData,
    var companyHistory: AdaptiveCompanyHistory,
    var companyNews: AdaptiveCompanyNews,
    var companyStyle: AdaptiveCompanyStyle,
    var isFavourite: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompany) {
            companyProfile.name == other.companyProfile.name &&
                    companyProfile.symbol == other.companyProfile.symbol &&
                    companyDayData.currentPrice == other.companyDayData.currentPrice
        } else false
    }

    override fun hashCode(): Int {
        return (companyProfile.name +
                companyDayData.currentPrice +
                companyDayData.lowPrice +
                "${companyHistory.closePrices}"
                ).hashCode()
    }
}