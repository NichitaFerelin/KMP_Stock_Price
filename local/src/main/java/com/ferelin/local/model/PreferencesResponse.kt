package com.ferelin.local.model

sealed class PreferencesResponse {
    data class Success<out T>(val data: T) : PreferencesResponse()
    object Failed : PreferencesResponse()
}