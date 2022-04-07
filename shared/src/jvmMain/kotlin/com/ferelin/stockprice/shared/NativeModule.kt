package com.ferelin.stockprice.shared

import com.ferelin.stockprice.db.StockPriceDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.client.engine.java.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun nativeModule(): Module = module {
  single<SqlDriver> {
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
      StockPriceDb.Schema.create(it)
    }
  }
  single { Java.create() }
}