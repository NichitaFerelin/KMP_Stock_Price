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