package com.ferelin.local.responses

sealed class SearchesResponse {
    class Success(val data: Set<String>) : SearchesResponse()
    object Failed : SearchesResponse()
}