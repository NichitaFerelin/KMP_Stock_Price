package stockprice

import kotlin.Int
import kotlin.String

public data class CryptoDBO(
  public val id: Int,
  public val name: String,
  public val ticker: String,
  public val logoUrl: String
) {
  public override fun toString(): String = """
  |CryptoDBO [
  |  id: $id
  |  name: $name
  |  ticker: $ticker
  |  logoUrl: $logoUrl
  |]
  """.trimMargin()
}
