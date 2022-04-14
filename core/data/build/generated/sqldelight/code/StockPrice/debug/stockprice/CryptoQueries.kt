package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Unit

public interface CryptoQueries : Transacter {
  public fun <T : Any> getAll(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String
  ) -> T): Query<T>

  public fun getAll(): Query<CryptoDBO>

  public fun insert(CryptoDBO: CryptoDBO): Unit
}
