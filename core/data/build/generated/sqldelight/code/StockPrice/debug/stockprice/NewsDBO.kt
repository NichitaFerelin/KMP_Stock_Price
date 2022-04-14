package stockprice

import kotlin.Int
import kotlin.Long
import kotlin.String

public data class NewsDBO(
  public val id: Long,
  public val companyId: Int,
  public val headline: String,
  public val source: String,
  public val sourceUrl: String,
  public val summary: String,
  public val dateMillis: Long
) {
  public override fun toString(): String = """
  |NewsDBO [
  |  id: $id
  |  companyId: $companyId
  |  headline: $headline
  |  source: $source
  |  sourceUrl: $sourceUrl
  |  summary: $summary
  |  dateMillis: $dateMillis
  |]
  """.trimMargin()
}
