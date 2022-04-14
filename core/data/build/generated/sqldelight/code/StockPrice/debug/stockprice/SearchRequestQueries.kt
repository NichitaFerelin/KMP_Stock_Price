package stockprice

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Unit

public interface SearchRequestQueries : Transacter {
  public fun <T : Any> getAll(mapper: (id: Int, request: String) -> T): Query<T>

  public fun getAll(): Query<SearchRequestDBO>

  public fun insert(id: Int?, request: String): Unit

  public fun eraseBy(id: Int): Unit
}
