package com.ferelin.stockprice.db.`data`

import com.ferelin.stockprice.db.StockPrice
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass
import stockprice.CompanyDBO
import stockprice.CompanyNewsDBO
import stockprice.CompanyNewsQueries
import stockprice.CompanyQueries
import stockprice.CryptoDBO
import stockprice.CryptoPriceDBO
import stockprice.CryptoPriceQueries
import stockprice.CryptoQueries
import stockprice.FavoriteCompanyQueries
import stockprice.GetAll
import stockprice.MarketNewsDBO
import stockprice.MarketNewsQueries
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

  public override val companyNewsQueries: CompanyNewsQueriesImpl = CompanyNewsQueriesImpl(this,
      driver)

  public override val cryptoQueries: CryptoQueriesImpl = CryptoQueriesImpl(this, driver)

  public override val cryptoPriceQueries: CryptoPriceQueriesImpl = CryptoPriceQueriesImpl(this,
      driver)

  public override val favoriteCompanyQueries: FavoriteCompanyQueriesImpl =
      FavoriteCompanyQueriesImpl(this, driver)

  public override val marketNewsQueries: MarketNewsQueriesImpl = MarketNewsQueriesImpl(this, driver)

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
          |    capitalization TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE CompanyNewsDBO(
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
          |CREATE TABLE FavoriteCompanyDBO(
          |    insertOrder INTEGER PRIMARY KEY AUTOINCREMENT,
          |    companyOwnerId INTEGER NOT NULL,
          |    FOREIGN KEY (companyOwnerId) REFERENCES CompanyDBO(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE MarketNewsDBO(
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    headline TEXT NOT NULL,
          |    category TEXT NOT NULL,
          |    summary TEXT NOT NULL,
          |    imageUrl TEXT NOT NULL,
          |    url TEXT NOT NULL,
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
  internal val getBy: MutableList<Query<*>> = copyOnWriteList()

  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getBy(id: Int, mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String
  ) -> T): Query<T> = GetByQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)!!,
      cursor.getString(8)!!
    )
  }

  public override fun getBy(id: Int): Query<CompanyDBO> = getBy(id) { id_, name, ticker, logoUrl,
      industry, country, phone, webUrl, capitalization ->
    CompanyDBO(
      id_,
      name,
      ticker,
      logoUrl,
      industry,
      country,
      phone,
      webUrl,
      capitalization
    )
  }

  public override fun <T : Any> getAll(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String
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
      cursor.getString(8)!!
    )
  }

  public override fun getAll(): Query<CompanyDBO> = getAll { id, name, ticker, logoUrl, industry,
      country, phone, webUrl, capitalization ->
    CompanyDBO(
      id,
      name,
      ticker,
      logoUrl,
      industry,
      country,
      phone,
      webUrl,
      capitalization
    )
  }

  public override fun insert(CompanyDBO: CompanyDBO): Unit {
    driver.execute(1634774877,
        """INSERT OR REPLACE INTO CompanyDBO VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""", 9) {
      bindLong(1, CompanyDBO.id.toLong())
      bindString(2, CompanyDBO.name)
      bindString(3, CompanyDBO.ticker)
      bindString(4, CompanyDBO.logoUrl)
      bindString(5, CompanyDBO.industry)
      bindString(6, CompanyDBO.country)
      bindString(7, CompanyDBO.phone)
      bindString(8, CompanyDBO.webUrl)
      bindString(9, CompanyDBO.capitalization)
    }
    notifyQueries(1634774877, {database.favoriteCompanyQueries.getAll +
        database.companyQueries.getBy + database.companyQueries.getAll})
  }

  private inner class GetByQuery<out T : Any>(
    public val id: Int,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getBy, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1436092713,
        """SELECT * FROM CompanyDBO WHERE id = ?""", 1) {
      bindLong(1, id.toLong())
    }

    public override fun toString(): String = "company.sq:getBy"
  }
}

private class CompanyNewsQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CompanyNewsQueries {
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

  public override fun getAllBy(companyId: Int): Query<CompanyNewsDBO> = getAllBy(companyId) { id,
      companyId_, headline, source, sourceUrl, summary, dateMillis ->
    CompanyNewsDBO(
      id,
      companyId_,
      headline,
      source,
      sourceUrl,
      summary,
      dateMillis
    )
  }

  public override fun insert(CompanyNewsDBO: CompanyNewsDBO): Unit {
    driver.execute(751087504,
        """INSERT OR REPLACE INTO CompanyNewsDBO VALUES (?, ?, ?, ?, ?, ?, ?)""", 7) {
      bindLong(1, CompanyNewsDBO.id)
      bindLong(2, CompanyNewsDBO.companyId.toLong())
      bindString(3, CompanyNewsDBO.headline)
      bindString(4, CompanyNewsDBO.source)
      bindString(5, CompanyNewsDBO.sourceUrl)
      bindString(6, CompanyNewsDBO.summary)
      bindLong(7, CompanyNewsDBO.dateMillis)
    }
    notifyQueries(751087504, {database.companyNewsQueries.getAllBy})
  }

  public override fun eraseAllBy(companyId: Int): Unit {
    driver.execute(-760906711, """DELETE FROM CompanyNewsDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }
    notifyQueries(-760906711, {database.companyNewsQueries.getAllBy})
  }

  private inner class GetAllByQuery<out T : Any>(
    public val companyId: Int,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getAllBy, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1647531833,
        """SELECT * FROM CompanyNewsDBO WHERE companyId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }

    public override fun toString(): String = "companyNews.sq:getAllBy"
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

private class FavoriteCompanyQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), FavoriteCompanyQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    insertOrder: Int,
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String
  ) -> T): Query<T> = Query(-1698943021, getAll, driver, "favoriteCompany.sq", "getAll", """
  |SELECT FavoriteCompanyDBO.insertOrder,
  |CompanyDBO.id,
  |CompanyDBO.name,
  |CompanyDBO.ticker,
  |CompanyDBO.logoUrl,
  |CompanyDBO.industry,
  |CompanyDBO.country,
  |CompanyDBO.phone,
  |CompanyDBO.webUrl,
  |CompanyDBO.capitalization
  |FROM FavoriteCompanyDBO
  |INNER JOIN CompanyDBO ON FavoriteCompanyDBO.companyOwnerId = CompanyDBO.id
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getLong(1)!!.toInt(),
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)!!,
      cursor.getString(8)!!,
      cursor.getString(9)!!
    )
  }

  public override fun getAll(): Query<GetAll> = getAll { insertOrder, id, name, ticker, logoUrl,
      industry, country, phone, webUrl, capitalization ->
    GetAll(
      insertOrder,
      id,
      name,
      ticker,
      logoUrl,
      industry,
      country,
      phone,
      webUrl,
      capitalization
    )
  }

  public override fun eraseBy(companyId: Int): Unit {
    driver.execute(1748447541, """DELETE FROM FavoriteCompanyDBO WHERE companyOwnerId = ?""", 1) {
      bindLong(1, companyId.toLong())
    }
    notifyQueries(1748447541, {database.favoriteCompanyQueries.getAll})
  }

  public override fun insert(insertOrder: Int?, companyOwnerId: Int): Unit {
    driver.execute(-1633368031, """INSERT OR REPLACE INTO FavoriteCompanyDBO VALUES (?, ?)""", 2) {
      bindLong(1, insertOrder?.let { it.toLong() })
      bindLong(2, companyOwnerId.toLong())
    }
    notifyQueries(-1633368031, {database.favoriteCompanyQueries.getAll})
  }
}

private class MarketNewsQueriesImpl(
  private val database: StockPriceImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), MarketNewsQueries {
  internal val getAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getAll(mapper: (
    id: Long,
    headline: String,
    category: String,
    summary: String,
    imageUrl: String,
    url: String,
    dateMillis: Long
  ) -> T): Query<T> = Query(1258334767, getAll, driver, "marketNews.sq", "getAll",
      "SELECT * FROM MarketNewsDBO") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!
    )
  }

  public override fun getAll(): Query<MarketNewsDBO> = getAll { id, headline, category, summary,
      imageUrl, url, dateMillis ->
    MarketNewsDBO(
      id,
      headline,
      category,
      summary,
      imageUrl,
      url,
      dateMillis
    )
  }

  public override fun insert(MarketNewsDBO: MarketNewsDBO): Unit {
    driver.execute(1323909757,
        """INSERT OR REPLACE INTO MarketNewsDBO VALUES (?, ?, ?, ?, ?, ?, ?)""", 7) {
      bindLong(1, MarketNewsDBO.id)
      bindString(2, MarketNewsDBO.headline)
      bindString(3, MarketNewsDBO.category)
      bindString(4, MarketNewsDBO.summary)
      bindString(5, MarketNewsDBO.imageUrl)
      bindString(6, MarketNewsDBO.url)
      bindLong(7, MarketNewsDBO.dateMillis)
    }
    notifyQueries(1323909757, {database.marketNewsQueries.getAll})
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
  internal val getBy: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getBy(id: Int, mapper: (
    id: Int,
    currentPrice: Double,
    previousClosePrice: Double,
    openPrice: Double,
    highPrice: Double,
    lowPrice: Double
  ) -> T): Query<T> = GetByQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!.toInt(),
      cursor.getDouble(1)!!,
      cursor.getDouble(2)!!,
      cursor.getDouble(3)!!,
      cursor.getDouble(4)!!,
      cursor.getDouble(5)!!
    )
  }

  public override fun getBy(id: Int): Query<StockPriceDBO> = getBy(id) { id_, currentPrice,
      previousClosePrice, openPrice, highPrice, lowPrice ->
    StockPriceDBO(
      id_,
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
    notifyQueries(-1832978399, {database.stockPriceQueries.getBy})
  }

  private inner class GetByQuery<out T : Any>(
    public val id: Int,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getBy, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-2000906267,
        """SELECT * FROM StockPriceDBO WHERE id = ?""", 1) {
      bindLong(1, id.toLong())
    }

    public override fun toString(): String = "stockPrice.sq:getBy"
  }
}
