package com.ferelin.stockprice.db.`data`

import com.ferelin.stockprice.db.StockPrice
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass
import stockprice.CompanyDBO
import stockprice.CompanyQueries
import stockprice.CryptoDBO
import stockprice.CryptoPriceDBO
import stockprice.CryptoPriceQueries
import stockprice.CryptoQueries
import stockprice.NewsDBO
import stockprice.NewsQueries
import stockprice.PastPriceDBO
import stockprice.PastPriceQueries
import stockprice.SearchRequestDBO
import stockprice.SearchRequestQueries
import stockprice.StockPriceDBO
import stockprice.StockPriceQueries

internal val KClass<StockPrice>.schema: SqlDriver.Schema
  get() = StockPriceImpl.Schema

internal fun KClass<StockPrice>.newInstance(driver: SqlDriver): StockPrice = StockPriceImpl(driver)

private class StockPriceImpl(
  driver: SqlDriver
) : TransacterImpl(driver), StockPrice {
  public override val companyQueries: CompanyQueriesImpl = CompanyQueriesImpl(this, driver)

  public override val cryptoQueries: CryptoQueriesImpl = CryptoQueriesImpl(this, driver)

  public override val cryptoPriceQueries: CryptoPriceQueriesImpl = CryptoPriceQueriesImpl(this,
      driver)

  public override val newsQueries: NewsQueriesImpl = NewsQueriesImpl(this, driver)

  public override val pastPriceQueries: PastPriceQueriesImpl = PastPriceQueriesImpl(this, driver)

  public override val searchRequestQueries: SearchRequestQueriesImpl =
      SearchRequestQueriesImpl(this, driver)

  public override val stockPriceQueries: StockPriceQueriesImpl = StockPriceQueriesImpl(this, driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE CompanyDBO(
          |    id INTEGER PRIMARY KEY,
          |    name TEXT NOT NULL,
          |    ticker TEXT NOT NULL,
          |    logoUrl TEXT NOT NULL,
          |    industry TEXT NOT NULL,
          |    country TEXT NOT NULL,
          |    phone TEXT NOT NULL,
          |    webUrl TEXT NOT NULL,
          |    capitalization TEXT NOT NULL,
          |    isFavourite INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CryptoDBO(
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    name TEXT NOT NULL,
          |    ticker TEXT NOT NULL,
          |    logoUrl TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CryptoPriceDBO(
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    price REAL NOT NULL,
          |    priceChange REAL NOT NULL,
          |    priceChangePercents REAL NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE NewsDBO(
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    companyId INTEGER NOT NULL,
          |    headline TEXT NOT NULL,
          |    source TEXT NOT NULL,
          |    sourceUrl TEXT NOT NULL,
          |    summary TEXT NOT NULL,
          |    dateMillis INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE PastPriceDBO(
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    companyId INTEGER NOT NULL,
          |    closePrice REAL NOT NULL,
          |    dateMillis INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE SearchRequestDBO(
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    request TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE StockPriceDBO(
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    currentPrice REAL NOT NULL,
          |    previousClosePrice REAL NOT NULL,
          |    openPrice REAL NOT NULL,
          |    highPrice REAL NOT NULL,
          |    lowPrice REAL NOT NULL
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
    }
  }
}

private class CompanyQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CompanyQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  internal val getAllFavourites: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String,
    isFavourite: Boolean
  ) -> T): Query<T> = Query(1569199887, getAll, driver, "company.sq", "getAll",
      "SELECT * FROM CompanyDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)!!,
      cursor.getString(8)!!,
      cursor.getLong(9)!! == 1L
    )
  }

  public override fun getAll(): Query<CompanyDBO> = getAll { id, name, ticker, logoUrl, industry,
      country, phone, webUrl, capitalization, isFavourite ->
    CompanyDBO(
      id,
      name,
      ticker,
      logoUrl,
      industry,
      country,
      phone,
      webUrl,
      capitalization,
      isFavourite
    )
  }

  public override fun <T : Any> getAllFavourites(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String,
    isFavourite: Boolean
  ) -> T): Query<T> = Query(-222420647, getAllFavourites, driver, "company.sq", "getAllFavourites",
      "SELECT * FROM CompanyDBO WHERE isFavourite = 1") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)!!,
      cursor.getString(8)!!,
      cursor.getLong(9)!! == 1L
    )
  }

  public override fun getAllFavourites(): Query<CompanyDBO> = getAllFavourites { id, name, ticker,
      logoUrl, industry, country, phone, webUrl, capitalization, isFavourite ->
    CompanyDBO(
      id,
      name,
      ticker,
      logoUrl,
      industry,
      country,
      phone,
      webUrl,
      capitalization,
      isFavourite
    )
  }

  public override fun insert(CompanyDBO: CompanyDBO): Unit {
    driver.execute(1634774877,
        """INSERT OR REPLACE INTO CompanyDBO VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""", 10) {
      bindLong(1, CompanyDBO.id.toLong())
      bindString(2, CompanyDBO.name)
      bindString(3, CompanyDBO.ticker)
      bindString(4, CompanyDBO.logoUrl)
      bindString(5, CompanyDBO.industry)
      bindString(6, CompanyDBO.country)
      bindString(7, CompanyDBO.phone)
      bindString(8, CompanyDBO.webUrl)
      bindString(9, CompanyDBO.capitalization)
      bindLong(10, if (CompanyDBO.isFavourite) 1L else 0L)
    }
    notifyQueries(1634774877, {database.companyQueries.getAllFavourites +
        database.companyQueries.getAll})
  }
}

private class CryptoQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CryptoQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String
  ) -> T): Query<T> = Query(-1525849407, getAll, driver, "crypto.sq", "getAll",
      "SELECT * FROM CryptoDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!
    )
  }

  public override fun getAll(): Query<CryptoDBO> = getAll { id, name, ticker, logoUrl ->
    CryptoDBO(
      id,
      name,
      ticker,
      logoUrl
    )
  }

  public override fun insert(CryptoDBO: CryptoDBO): Unit {
    driver.execute(-1460274417, """INSERT OR REPLACE INTO CryptoDBO VALUES (?, ?, ?, ?)""", 4) {
      bindLong(1, CryptoDBO.id.toLong())
      bindString(2, CryptoDBO.name)
      bindString(3, CryptoDBO.ticker)
      bindString(4, CryptoDBO.logoUrl)
    }
    notifyQueries(-1460274417, {database.cryptoQueries.getAll})
  }
}

private class CryptoPriceQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CryptoPriceQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    id: Int,
    price: Double,
    priceChange: Double,
    priceChangePercents: Double
  ) -> T): Query<T> = Query(-1130281638, getAll, driver, "cryptoPrice.sq", "getAll",
      "SELECT * FROM CryptoPriceDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getDouble(1)!!,
      cursor.getDouble(2)!!,
      cursor.getDouble(3)!!
    )
  }

  public override fun getAll(): Query<CryptoPriceDBO> = getAll { id, price, priceChange,
      priceChangePercents ->
    CryptoPriceDBO(
      id,
      price,
      priceChange,
      priceChangePercents
    )
  }

  public override fun insert(CryptoPriceDBO: CryptoPriceDBO): Unit {
    driver.execute(-1064706648, """INSERT OR REPLACE INTO CryptoPriceDBO VALUES (?, ?, ?, ?)""", 4)
        {
      bindLong(1, CryptoPriceDBO.id.toLong())
      bindDouble(2, CryptoPriceDBO.price)
      bindDouble(3, CryptoPriceDBO.priceChange)
      bindDouble(4, CryptoPriceDBO.priceChangePercents)
    }
    notifyQueries(-1064706648, {database.cryptoPriceQueries.getAll})
  }
}

private class NewsQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), NewsQueries {
  internal val getAllBy: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAllBy(companyId: Int, mapper: (
    id: Long,
    companyId: Int,
    headline: String,
    source: String,
    sourceUrl: String,
    summary: String,
    dateMillis: Long
  ) -> T): Query<T> = GetAllByQuery(companyId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!.toInt(),
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!
    )
  }

  public override fun getAllBy(companyId: Int): Query<NewsDBO> = getAllBy(companyId) { id,
      companyId_, headline, source, sourceUrl, summary, dateMillis ->
    NewsDBO(
      id,
      companyId_,
      headline,
      source,
      sourceUrl,
      summary,
      dateMillis
    )
  }

  public override fun insert(NewsDBO: NewsDBO): Unit {
    driver.execute(-851270559, """INSERT OR REPLACE INTO NewsDBO VALUES (?, ?, ?, ?, ?, ?, ?)""", 7)
        {
      bindLong(1, NewsDBO.id)
      bindLong(2, NewsDBO.companyId.toLong())
      bindString(3, NewsDBO.headline)
      bindString(4, NewsDBO.source)
      bindString(5, NewsDBO.sourceUrl)
      bindString(6, NewsDBO.summary)
      bindLong(7, NewsDBO.dateMillis)
    }
    notifyQueries(-851270559, {database.newsQueries.getAllBy})
  }

  public override fun eraseAllBy(companyId: Int): Unit {
    driver.execute(1720361082, """DELETE FROM NewsDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }
    notifyQueries(1720361082, {database.newsQueries.getAllBy})
  }

  private inner class GetAllByQuery<out T : Any>(
    public val companyId: Int,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getAllBy, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-620274742,
        """SELECT * FROM NewsDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }

    public override fun toString(): String = "news.sq:getAllBy"
  }
}

private class PastPriceQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), PastPriceQueries {
  internal val getAllBy: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAllBy(companyId: Int, mapper: (
    id: Long,
    companyId: Int,
    closePrice: Double,
    dateMillis: Long
  ) -> T): Query<T> = GetAllByQuery(companyId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!.toInt(),
      cursor.getDouble(2)!!,
      cursor.getLong(3)!!
    )
  }

  public override fun getAllBy(companyId: Int): Query<PastPriceDBO> = getAllBy(companyId) { id,
      companyId_, closePrice, dateMillis ->
    PastPriceDBO(
      id,
      companyId_,
      closePrice,
      dateMillis
    )
  }

  public override fun insert(
    id: Long?,
    companyId: Int,
    closePrice: Double,
    dateMillis: Long
  ): Unit {
    driver.execute(-2103191433, """INSERT OR REPLACE INTO PastPriceDBO VALUES (?, ?, ?, ?)""", 4) {
      bindLong(1, id)
      bindLong(2, companyId.toLong())
      bindDouble(3, closePrice)
      bindLong(4, dateMillis)
    }
    notifyQueries(-2103191433, {database.pastPriceQueries.getAllBy})
  }

  public override fun eraseAllBy(companyId: Int): Unit {
    driver.execute(1634195856, """DELETE FROM PastPriceDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }
    notifyQueries(1634195856, {database.pastPriceQueries.getAllBy})
  }

  private inner class GetAllByQuery<out T : Any>(
    public val companyId: Int,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getAllBy, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-1125391776,
        """SELECT * FROM PastPriceDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }

    public override fun toString(): String = "pastPrice.sq:getAllBy"
  }
}

private class SearchRequestQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), SearchRequestQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (id: Int, request: String) -> T): Query<T> =
      Query(829157145, getAll, driver, "searchRequest.sq", "getAll",
      "SELECT * FROM SearchRequestDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getString(1)!!
    )
  }

  public override fun getAll(): Query<SearchRequestDBO> = getAll { id, request ->
    SearchRequestDBO(
      id,
      request
    )
  }

  public override fun insert(id: Int?, request: String): Unit {
    driver.execute(894732135, """INSERT OR REPLACE INTO SearchRequestDBO VALUES (?, ?)""", 2) {
      bindLong(1, id?.let { it.toLong() })
      bindString(2, request)
    }
    notifyQueries(894732135, {database.searchRequestQueries.getAll})
  }

  public override fun eraseBy(id: Int): Unit {
    driver.execute(-1484825937, """DELETE FROM SearchRequestDBO WHERE id = ?""", 1) {
      bindLong(1, id.toLong())
    }
    notifyQueries(-1484825937, {database.searchRequestQueries.getAll})
  }
}

private class StockPriceQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), StockPriceQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    id: Int,
    currentPrice: Double,
    previousClosePrice: Double,
    openPrice: Double,
    highPrice: Double,
    lowPrice: Double
  ) -> T): Query<T> = Query(-1898553389, getAll, driver, "stockPrice.sq", "getAll",
      "SELECT * FROM StockPriceDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getDouble(1)!!,
      cursor.getDouble(2)!!,
      cursor.getDouble(3)!!,
      cursor.getDouble(4)!!,
      cursor.getDouble(5)!!
    )
  }

  public override fun getAll(): Query<StockPriceDBO> = getAll { id, currentPrice,
      previousClosePrice, openPrice, highPrice, lowPrice ->
    StockPriceDBO(
      id,
      currentPrice,
      previousClosePrice,
      openPrice,
      highPrice,
      lowPrice
    )
  }

  public override fun insert(StockPriceDBO: StockPriceDBO): Unit {
    driver.execute(-1832978399,
        """INSERT OR REPLACE INTO StockPriceDBO VALUES (?, ?, ?, ?, ?, ?)""", 6) {
      bindLong(1, StockPriceDBO.id.toLong())
      bindDouble(2, StockPriceDBO.currentPrice)
      bindDouble(3, StockPriceDBO.previousClosePrice)
      bindDouble(4, StockPriceDBO.openPrice)
      bindDouble(5, StockPriceDBO.highPrice)
      bindDouble(6, StockPriceDBO.lowPrice)
    }
    notifyQueries(-1832978399, {database.stockPriceQueries.getAll})
  }
}
