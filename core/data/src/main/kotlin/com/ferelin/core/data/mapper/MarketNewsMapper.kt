package com.ferelin.core.data.mapper

import com.ferelin.core.data.api.toUnixTime
import com.ferelin.core.data.entity.marketNews.MarketNewsPojo
import com.ferelin.core.domain.entity.MarketNews
import com.ferelin.core.domain.entity.MarketNewsId
import stockprice.MarketNewsDBO

internal object MarketNewsMapper {
    fun map(marketNewsDBO: MarketNewsDBO): MarketNews {
        return MarketNews(
            id = MarketNewsId(marketNewsDBO.id),
            category = marketNewsDBO.category,
            headline = marketNewsDBO.headline,
            summary = marketNewsDBO.summary,
            url = marketNewsDBO.url,
            imageUrl = marketNewsDBO.imageUrl,
            dateMillis = marketNewsDBO.dateMillis
        )
    }

    fun map(marketNewsPojo: MarketNewsPojo): MarketNewsDBO {
        return MarketNewsDBO(
            id = marketNewsPojo.id,
            category = marketNewsPojo.category,
            headline = marketNewsPojo.headline,
            summary = marketNewsPojo.summary,
            url = marketNewsPojo.url,
            imageUrl = marketNewsPojo.imageUrl,
            dateMillis = marketNewsPojo.dateMillis.toUnixTime()
        )
    }
}