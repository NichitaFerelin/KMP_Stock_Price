package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface NewsQueries : Transacter {
  public fun <T : Any> getAllBy(companyId: Int, mapper: (
    id: Long,
    companyId: Int,
    headline: String,
    source: String,
    sourceUrl: String,
    summary: String,
    dateMillis: Long
  ) -> T): Query<T>

  public fun getAllBy(companyId: Int): Query<NewsDBO>

  public fun insert(NewsDBO: NewsDBO): Unit

  public fun eraseAllBy(companyId: Int): Unit
}
