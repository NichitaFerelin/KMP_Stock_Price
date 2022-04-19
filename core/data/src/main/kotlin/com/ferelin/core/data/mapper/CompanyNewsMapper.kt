package com.ferelin.core.data.mapper

import com.ferelin.core.data.api.toUnixTime
import com.ferelin.core.data.entity.companyNews.CompanyNewsPojo
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.CompanyNews
import com.ferelin.core.domain.entity.CompanyNewsId
import stockprice.CompanyNewsDBO

internal object CompanyNewsMapper {
    fun map(newsDBO: CompanyNewsDBO): CompanyNews {
        return CompanyNews(
            id = CompanyNewsId(newsDBO.id),
            companyId = CompanyId(newsDBO.companyId),
            headline = newsDBO.headline,
            source = newsDBO.source,
            sourceUrl = newsDBO.sourceUrl,
            summary = newsDBO.summary,
            dateMillis = newsDBO.dateMillis
        )
    }

    fun map(newsPojo: List<CompanyNewsPojo>, companyId: CompanyId): List<CompanyNewsDBO> {
        return newsPojo.map { pojo ->
            CompanyNewsDBO(
                id = pojo.id,
                companyId = companyId.value,
                headline = pojo.headline,
                source = pojo.source,
                sourceUrl = pojo.url,
                summary = pojo.summary,
                dateMillis = pojo.datetime.toUnixTime()
            )
        }
    }
}