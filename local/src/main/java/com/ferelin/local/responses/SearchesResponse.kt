package com.ferelin.local.responses

/**
 * [SearchesResponse] for search requests history
 */
sealed class SearchesResponse {
    class Success(val data: Set<String>) : SearchesResponse()
    object Failed : SearchesResponse()
}