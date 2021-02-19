package com.ferelin.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ferelin.local.model.Company

@Database(entities = [Company::class], version = 1)
@TypeConverters(CompaniesConverter::class)
abstract class CompaniesDatabase : RoomDatabase() {

    abstract fun companiesDao(): CompaniesDao

    companion object {
        private var sInstance: CompaniesDatabase? = null
        const val DB_NAME = "stockprice.companies.db"

        fun getDatabase(context: Context): CompaniesDatabase {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(
                    context.applicationContext,
                    CompaniesDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
            }
            return sInstance!!
        }
    }
}