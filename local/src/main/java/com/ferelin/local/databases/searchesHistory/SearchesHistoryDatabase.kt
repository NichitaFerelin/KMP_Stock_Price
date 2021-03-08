package com.ferelin.local.databases.searchesHistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ferelin.local.model.Search
import com.ferelin.shared.SingletonHolder

@Database(entities = [Search::class], version = 1)
abstract class SearchesHistoryDatabase : RoomDatabase() {

    abstract fun searchesDao(): SearchesHistoryDao

    companion object : SingletonHolder<SearchesHistoryDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            SearchesHistoryDatabase::class.java,
            Companion.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }) {
        const val DB_NAME = "stockprice.searches.db"
    }
}