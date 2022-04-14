package stockprice

import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class PastPriceDBO(
  public val id: Long,
  public val companyId: Int,
  public val closePrice: Double,
  public val dateMillis: Long
) {
  public override fun toString(): String = """
  |PastPriceDBO [
  |  id: $id
  |  companyId: $companyId
  |  closePrice: $closePrice
  |  dateMillis: $dateMillis
  |]
  """.trimMargin()
}
