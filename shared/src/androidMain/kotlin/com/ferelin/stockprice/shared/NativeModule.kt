package com.ferelin.stockprice.shared

import com.ferelin.stockprice.db.StockPriceDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.android.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun nativeModule(): Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            StockPriceDb.Schema,
            androidContext(),
            "StockPriceDb"
        )
    }
    single { Android.create() }
}