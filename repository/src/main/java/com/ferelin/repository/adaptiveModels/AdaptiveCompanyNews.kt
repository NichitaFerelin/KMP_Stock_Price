package com.ferelin.repository.adaptiveModels

class AdaptiveCompanyNews(
    var ids: List<String>,
    var headlines: List<String>,
    var summaries: List<String>,
    var sources: List<String>,
    var dates: List<String>,
    var browserUrls: List<String>,
    var previewImagesUrls: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompanyNews) {
            ids.firstOrNull() == other.ids.firstOrNull()
        } else false
    }

    override fun hashCode(): Int {
        var result = ids.hashCode()
        result = 31 * result + headlines.hashCode()
        result = 31 * result + summaries.hashCode()
        result = 31 * result + sources.hashCode()
        result = 31 * result + dates.hashCode()
        result = 31 * result + browserUrls.hashCode()
        result = 31 * result + previewImagesUrls.hashCode()
        return result
    }
}