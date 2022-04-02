package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.pastPrice.PastPricesApiSpecifics.fromRequestFormat
import com.ferelin.core.data.entity.pastPrice.PastPricesResponse
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.entity.PastPriceId
import stockprice.PastPriceDBO

internal object PastPriceMapper {
  fun map(pastPriceDBO: PastPriceDBO): PastPrice {
    return PastPrice(
      id = PastPriceId(pastPriceDBO.id),
      companyId = CompanyId(pastPriceDBO.companyId),
      closePrice = pastPriceDBO.closePrice,
      dateMillis = pastPriceDBO.dateMillis
    )
  }

  fun map(pastPricesResponse: PastPricesResponse, companyId: CompanyId): List<PastPriceDBO> {
    if (pastPricesResponse.closePrices.size != pastPricesResponse.timestamps.size) {
      return emptyList()
    }

    return pastPricesResponse.closePrices.mapIndexed { index, closePrice ->
      PastPriceDBO(
        id = 0,
        companyId = companyId.value,
        closePrice = closePrice,
        dateMillis = pastPricesResponse.timestamps[index].fromRequestFormat()
      )
    }
  }
}