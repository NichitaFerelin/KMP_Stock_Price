package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeStockViewData(
    val id: Int,
    val name: String,
    val industry: String,
    val isFavourite: Boolean,
    val logoUrl: String
)