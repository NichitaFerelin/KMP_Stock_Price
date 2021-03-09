package com.ferelin.repository.adaptiveModels

data class AdaptiveCompanyNews(
    var symbol: String,
    val date: List<String>,
    val headline: List<String>,
    val newsId: List<String>,
    val previewImageUrl: List<String>,
    val source: List<String>,
    val summary: List<String>,
    val url: List<String>
)