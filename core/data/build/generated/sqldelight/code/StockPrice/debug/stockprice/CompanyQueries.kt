package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit

public interface CompanyQueries : Transacter {
  public fun <T : Any> getAll(mapper: (
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
  ) -> T): Query<T>

  public fun getAll(): Query<CompanyDBO>

  public fun <T : Any> getAllFavourites(mapper: (
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
  ) -> T): Query<T>

  public fun getAllFavourites(): Query<CompanyDBO>

  public fun insert(CompanyDBO: CompanyDBO): Unit
}
