package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.Unit

public interface PastPriceQueries : Transacter {
  public fun <T : Any> getAllBy(companyId: Int, mapper: (
    id: Long,
    companyId: Int,
    closePrice: Double,
    dateMillis: Long
  ) -> T): Query<T>

  public fun getAllBy(companyId: Int): Query<PastPriceDBO>

  public fun insert(
    id: Long?,
    companyId: Int,
    closePrice: Double,
    dateMillis: Long
  ): Unit

  public fun eraseAllBy(companyId: Int): Unit
}
