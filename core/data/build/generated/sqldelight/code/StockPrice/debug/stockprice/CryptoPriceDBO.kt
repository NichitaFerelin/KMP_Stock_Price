package stockprice

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class CryptoPriceDBO(
  public val id: Int,
  public val price: Double,
  public val priceChange: Double,
  public val priceChangePercents: Double
) {
  public override fun toString(): String = """
  |CryptoPriceDBO [
  |  id: $id
  |  price: $price
  |  priceChange: $priceChange
  |  priceChangePercents: $priceChangePercents
  |]
  """.trimMargin()
}
