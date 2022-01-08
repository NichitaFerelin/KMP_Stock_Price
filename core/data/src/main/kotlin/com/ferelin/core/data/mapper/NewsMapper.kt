package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.news.NewsDBO
import com.ferelin.core.data.entity.news.NewsResponse
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.entity.NewsId

internal object NewsMapper {
  fun map(newsDBO: NewsDBO): News {
    return News(
      id = NewsId(newsDBO.id),
      companyId = CompanyId(newsDBO.companyId),
      headline = newsDBO.headline,
      previewImageUrl = newsDBO.previewImageUrl,
      source = newsDBO.source,
      sourceUrl = newsDBO.sourceUrl,
      summary = newsDBO.summary,
      date = newsDBO.date
    )
  }

  fun map(newsResponse: NewsResponse, companyId: CompanyId): List<NewsDBO> {
    return newsResponse.data.map { pojo ->
      NewsDBO(
        id = pojo.id,
        companyId = companyId.value,
        headline = pojo.headline,
        previewImageUrl = pojo.image,
        source = pojo.source,
        sourceUrl = pojo.url,
        summary = pojo.summary,
        date = pojo.datetime
      )
    }
  }
}