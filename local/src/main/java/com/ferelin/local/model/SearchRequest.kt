package com.ferelin.local.model

import androidx.room.PrimaryKey

data class SearchRequest(
    @PrimaryKey val searches: String
)