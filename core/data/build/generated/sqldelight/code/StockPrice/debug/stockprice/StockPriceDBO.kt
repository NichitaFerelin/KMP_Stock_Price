package stockprice

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class StockPriceDBO(
  public val id: Int,
  public val currentPrice: Double,
  public val previousClosePrice: Double,
  public val openPrice: Double,
  public val highPrice: Double,
  public val lowPrice: Double
) {
  public override fun toString(): String = """
  |StockPriceDBO [
  |  id: $id
  |  currentPrice: $currentPrice
  |  previousClosePrice: $previousClosePrice
  |  openPrice: $openPrice
  |  highPrice: $highPrice
  |  lowPrice: $lowPrice
  |]
  """.trimMargin()
}
