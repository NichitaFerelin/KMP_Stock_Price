package com.ferelin.core.data.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object AppDatabaseMigrations {
  val migration_1_to_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("DROP TABLE search_requests")
    }
  }
  val migration_2_to_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE TABLE IF NOT EXISTS `crypto` (" +
          "`name` TEXT NOT NULL, " +
          "`symbol` TEXT NOT NULL, " +
          "`crypto_id` INTEGER NOT NULL DEFAULT 0, " +
          "`logo_url` TEXT NOT NULL, " +
          "PRIMARY KEY(`crypto_id`)" +
          ")"
      )
      database.execSQL(
        "CREATE TABLE `crypto_prices` (" +
          "`relation_crypto_id` INTEGER NOT NULL DEFAULT 0, " +
          "`price` DOUBLE NOT NULL, " +
          "`price_timestamp` TEXT NOT NULL, " +
          "`high_price` DOUBLE NOT NULl, " +
          "`high_price_timestamp` TEXT NOT NULL, " +
          "`price_change` DOUBLE NOT NULL, " +
          "`price_change_percents` DOUBLE NOT NULL, " +
          "PRIMARY KEY(`relation_crypto_id`)" +
          ")"
      )
    }
  }
}