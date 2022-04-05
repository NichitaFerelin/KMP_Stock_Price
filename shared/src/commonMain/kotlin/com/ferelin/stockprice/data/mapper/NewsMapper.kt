package com.ferelin.stockprice.data.mapper

import com.ferelin.stockprice.data.entity.news.NewsPojo
import com.ferelin.stockprice.db.NewsDBO
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.News
import com.ferelin.stockprice.domain.entity.NewsId

internal object NewsMapper {
  fun map(newsDBO: NewsDBO): News {
    return News(
      id = NewsId(newsDBO.id),
      companyId = CompanyId(newsDBO.companyId),
      headline = newsDBO.headline,
      source = newsDBO.source,
      sourceUrl = newsDBO.sourceUrl,
      summary = newsDBO.summary,
      dateMillis = newsDBO.dateMillis
    )
  }

  fun map(newsPojo: List<NewsPojo>, companyId: CompanyId): List<NewsDBO> {
    return newsPojo.map { pojo ->
      NewsDBO(
        id = pojo.id,
        companyId = companyId.value,
        headline = pojo.headline,
        source = pojo.source,
        sourceUrl = pojo.url,
        summary = pojo.summary,
        dateMillis = pojo.datetime
      )
    }
  }
}