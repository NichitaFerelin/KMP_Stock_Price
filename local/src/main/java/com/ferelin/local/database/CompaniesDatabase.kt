package com.ferelin.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ferelin.local.model.Company
import com.ferelin.shared.SingletonHolder

@Database(entities = [Company::class], version = 1)
@TypeConverters(CompaniesConverter::class)
abstract class CompaniesDatabase : RoomDatabase() {

    abstract fun companiesDao(): CompaniesDao

    companion object : SingletonHolder<CompaniesDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            CompaniesDatabase::class.java,
            Companion.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }) {
        const val DB_NAME = "stockprice.companies.db"
    }
}