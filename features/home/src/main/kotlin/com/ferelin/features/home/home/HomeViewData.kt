package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable

@Immutable
internal data class HomeStockViewData(
    val id: Int,
    val name: String,
    val industry: String,
    val isFavorite: Boolean,
    val logoUrl: String
)