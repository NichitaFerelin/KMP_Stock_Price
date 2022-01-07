package com.ferelin.features.about.data.mapper

import com.ferelin.core.domain.entities.CompanyId
import com.ferelin.features.about.data.entity.pastPrice.PastPriceDBO
import com.ferelin.features.about.data.entity.pastPrice.PastPricesResponse
import com.ferelin.features.about.domain.entities.PastPrice
import com.ferelin.features.about.domain.entities.PastPriceId

internal object PastPriceMapper {
  fun map(pastPriceDBO: PastPriceDBO): PastPrice {
    return PastPrice(
      id = PastPriceId(pastPriceDBO.id),
      companyId = CompanyId(pastPriceDBO.companyId.value),
      openPrice = pastPriceDBO.openPrice,
      highPrice = pastPriceDBO.highPrice,
      lowPrice = pastPriceDBO.lowPrice,
      closePrice = pastPriceDBO.closePrice,
      dateMillis = pastPriceDBO.dateMillis
    )
  }

  fun map(pastPricesResponse: PastPricesResponse, companyId: CompanyId): List<PastPriceDBO> {
    with(pastPricesResponse) {
      if (
        closePrices.size != highPrices.size
        || highPrices.size != lowPrices.size
        || lowPrices.size != openPrices.size
        || openPrices.size != timestamps.size
        || timestamps.size != closePrices.size
      ) return emptyList()

      return List(timestamps.size) { index ->
        val openPrice = openPrices[index]
        val highPrice = highPrices[index]
        val lowPrice = lowPrices[index]
        val closePrice = closePrices[index]
        val timestamp = timestamps[index]

        PastPriceDBO(
          companyId = companyId,
          openPrice = openPrice,
          highPrice = highPrice,
          lowPrice = lowPrice,
          closePrice = closePrice,
          dateMillis = 0L // TODO timestamp.toBasicMillisTime()
        )
      }
    }
  }
}