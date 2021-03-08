package com.ferelin.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.local.databases.searchesHistory.SearchesHistoryDatabase

@Entity(tableName = SearchesHistoryDatabase.DB_NAME)
data class Search(
    @PrimaryKey val tickerName: String
)