package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.news.NewsPojo
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.entity.NewsId
import stockprice.NewsDBO

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