package com.ferelin.core.data.entity.pastPrice

import com.ferelin.core.ONE_YEAR_MILLIS
import com.ferelin.core.data.api.endPoints.pastPrice
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface PastPriceApi {
  suspend fun load(options: PastPricesOptions): PastPricesResponse
}

internal class PastPriceApiImpl(
  private val client: HttpClient
) : PastPriceApi {
  override suspend fun load(options: PastPricesOptions): PastPricesResponse {
    return client.get { pastPrice(options) }
  }
}

internal data class PastPricesOptions(
  val token: String,
  val companyTicker: String,
  val from: Long = PastPricesApiSpecifics.yearAgoMillis,
  val to: Long = PastPricesApiSpecifics.currentMillis,
  val resolution: String = PastPricesApiSpecifics.resolution
)

@kotlinx.serialization.Serializable
internal data class PastPricesResponse(
  @SerialName(value = "c") val closePrices: List<Double>,
  @SerialName(value = "t") val timestamps: List<Long>,
)

internal object PastPricesApiSpecifics {
  const val resolution = "D"

  val currentMillis: Long
    get() = System.currentTimeMillis().toRequestFormat()

  val yearAgoMillis: Long
    get() = (System.currentTimeMillis() - ONE_YEAR_MILLIS).toRequestFormat()

  fun Long.fromRequestFormat(): Long {
    return "${this}000".toLong()
  }

  private fun Long.toRequestFormat(): Long {
    val str = this.toString()
    return when {
      str.length < 3 -> this
      else -> str.substring(0, str.length - 3).toLong()
    }
  }
}