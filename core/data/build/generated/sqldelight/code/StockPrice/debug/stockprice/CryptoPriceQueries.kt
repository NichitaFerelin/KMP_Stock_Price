package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Double
import kotlin.Int
import kotlin.Unit

public interface CryptoPriceQueries : Transacter {
  public fun <T : Any> getAll(mapper: (
    id: Int,
    price: Double,
    priceChange: Double,
    priceChangePercents: Double
  ) -> T): Query<T>

  public fun getAll(): Query<CryptoPriceDBO>

  public fun insert(CryptoPriceDBO: CryptoPriceDBO): Unit
}
