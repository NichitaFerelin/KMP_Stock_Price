package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Double
import kotlin.Int
import kotlin.Unit

public interface StockPriceQueries : Transacter {
  public fun <T : Any> getAll(mapper: (
    id: Int,
    currentPrice: Double,
    previousClosePrice: Double,
    openPrice: Double,
    highPrice: Double,
    lowPrice: Double
  ) -> T): Query<T>

  public fun getAll(): Query<StockPriceDBO>

  public fun insert(StockPriceDBO: StockPriceDBO): Unit
}
