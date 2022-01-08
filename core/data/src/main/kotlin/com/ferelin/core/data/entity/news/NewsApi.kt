package com.ferelin.core.data.entity.news

import com.ferelin.core.ONE_YEAR_MILLIS
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

internal interface NewsApi {
  @GET("company-news")
  fun load(
    @Query("symbol") companyTicker: String,
    @Query("from") from: String = NewsApiSettings.yearAgoDate,
    @Query("to") to: String = NewsApiSettings.currentDate
  ): NewsResponse
}

@JsonClass(generateAdapter = true)
internal data class NewsResponse(
  val data: List<NewsPojo>
)

@JsonClass(generateAdapter = true)
internal data class NewsPojo(
  val id: String,
  val datetime: Double,
  val headline: String,
  val image: String,
  val source: String,
  val url: String,
  val summary: String
)

internal object NewsApiSettings {
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
}