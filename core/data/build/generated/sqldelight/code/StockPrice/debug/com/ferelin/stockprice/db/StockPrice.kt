package com.ferelin.stockprice.db

import com.ferelin.stockprice.db.`data`.newInstance
import com.ferelin.stockprice.db.`data`.schema
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver
import stockprice.CompanyQueries
import stockprice.CryptoPriceQueries
import stockprice.CryptoQueries
import stockprice.FavoriteCompanyQueries
import stockprice.NewsQueries
import stockprice.PastPriceQueries
import stockprice.SearchRequestQueries
import stockprice.StockPriceQueries

public interface StockPrice : Transacter {
  public val companyQueries: CompanyQueries

  public val cryptoQueries: CryptoQueries

  public val cryptoPriceQueries: CryptoPriceQueries

  public val favoriteCompanyQueries: FavoriteCompanyQueries

  public val newsQueries: NewsQueries

  public val pastPriceQueries: PastPriceQueries

  public val searchRequestQueries: SearchRequestQueries

  public val stockPriceQueries: StockPriceQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = StockPrice::class.schema

    public operator fun invoke(driver: SqlDriver): StockPrice =
        StockPrice::class.newInstance(driver)
  }
}
