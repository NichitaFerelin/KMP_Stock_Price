package stockprice

import kotlin.Int
import kotlin.String

public data class CompanyDBO(
  public val id: Int,
  public val name: String,
  public val ticker: String,
  public val logoUrl: String,
  public val industry: String,
  public val country: String,
  public val phone: String,
  public val webUrl: String,
  public val capitalization: String
) {
  public override fun toString(): String = """
  |CompanyDBO [
  |  id: $id
  |  name: $name
  |  ticker: $ticker
  |  logoUrl: $logoUrl
  |  industry: $industry
  |  country: $country
  |  phone: $phone
  |  webUrl: $webUrl
  |  capitalization: $capitalization
  |]
  """.trimMargin()
}
