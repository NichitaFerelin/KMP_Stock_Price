package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Unit

public interface CompanyQueries : Transacter {
  public fun <T : Any> getBy(id: Int, mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String
  ) -> T): Query<T>

  public fun getBy(id: Int): Query<CompanyDBO>

  public fun <T : Any> getAll(mapper: (
    id: Int,
    name: String,
    ticker: String,
    logoUrl: String,
    industry: String,
    country: String,
    phone: String,
    webUrl: String,
    capitalization: String
  ) -> T): Query<T>

  public fun getAll(): Query<CompanyDBO>

  public fun insert(CompanyDBO: CompanyDBO): Unit
}
