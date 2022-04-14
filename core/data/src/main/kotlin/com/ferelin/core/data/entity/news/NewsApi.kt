package com.ferelin.core.data.entity.news

import com.ferelin.core.ONE_YEAR_MILLIS
import com.ferelin.core.data.api.endPoints.news
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import java.text.SimpleDateFormat
import java.util.*

internal interface NewsApi {
    suspend fun load(options: NewsRequestOptions): List<NewsPojo>
}

internal class NewsApiImpl(
    private val client: HttpClient
) : NewsApi {
    override suspend fun load(options: NewsRequestOptions): List<NewsPojo> {
        return client.get { news(options) }.body()
    }
}

internal data class NewsRequestOptions(
    val token: String,
    val companyTicker: String,
    val from: String = NewsApiSpecifications.yearAgoDate,
    val to: String = NewsApiSpecifications.currentDate
)

@kotlinx.serialization.Serializable
internal data class NewsPojo(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "datetime") val datetime: Long,
    @SerialName(value = "headline") val headline: String,
    @SerialName(value = "source") val source: String,
    @SerialName(value = "url") val url: String,
    @SerialName(value = "summary") val summary: String
)

internal object NewsApiSpecifications {
    val currentDate: String
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val currentTimeMillis = System.currentTimeMillis()
            val date = Date(currentTimeMillis)
            return dateFormat.format(date)
        }

    val yearAgoDate: String
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val currentTimeMillis = System.currentTimeMillis()
            val yearAgoTimeMillis = currentTimeMillis - ONE_YEAR_MILLIS
            val yearAgoDate = Date(yearAgoTimeMillis)
            return dateFormat.format(yearAgoDate)
        }

    fun convertToUnixTime(pojo: NewsPojo): NewsPojo {
        return pojo.copy(
            datetime = (pojo.datetime.toString() + "000").toLong()
        )
    }
}