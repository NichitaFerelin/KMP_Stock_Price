package stockprice

import kotlin.Int
import kotlin.String

public data class SearchRequestDBO(
  public val id: Int,
  public val request: String
) {
  public override fun toString(): String = """
  |SearchRequestDBO [
  |  id: $id
  |  request: $request
  |]
  """.trimMargin()
}
