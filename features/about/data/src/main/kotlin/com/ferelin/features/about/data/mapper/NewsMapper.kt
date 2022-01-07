package com.ferelin.features.about.data.mapper

import com.ferelin.core.domain.entities.CompanyId
import com.ferelin.features.about.data.entity.news.NewsDBO
import com.ferelin.features.about.data.entity.news.NewsResponse
import com.ferelin.features.about.domain.entities.News
import com.ferelin.features.about.domain.entities.NewsId
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

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
        id = pojo.id.value,
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

internal class NewsAdapter {
  @FromJson
  fun fromJson(newsId: NewsId): String {
    return newsId.value
  }
}