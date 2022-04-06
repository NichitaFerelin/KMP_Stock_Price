package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.androidApp.data.entity.pastPrice.PastPricesApiSpecifics.fromRequestFormat
import com.ferelin.stockprice.androidApp.data.entity.pastPrice.PastPricesResponse
import com.ferelin.stockprice.db.PastPriceDBO
import com.ferelin.stockprice.androidApp.domain.entity.CompanyId
import com.ferelin.stockprice.androidApp.domain.entity.PastPrice
import com.ferelin.stockprice.androidApp.domain.entity.PastPriceId

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
        id = 0L,
        companyId = companyId.value,
        closePrice = closePrice,
        dateMillis = pastPricesResponse.timestamps[index].fromRequestFormat()
      )
    }
  }
}