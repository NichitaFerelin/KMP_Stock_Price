package com.ferelin.core.data.entity.news

import com.ferelin.core.ONE_YEAR_MILLIS
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

internal interface NewsApi {
  @GET("company-news")
  fun load(
    @Query("token") token: String,
    @Query("symbol") companyTicker: String,
    @Query("from") from: String = NewsApiSpecifications.yearAgoDate,
    @Query("to") to: String = NewsApiSpecifications.currentDate
  ): Single<List<NewsPojo>>
}

@JsonClass(generateAdapter = true)
internal data class NewsPojo(
  @Json(name = "id") val id: String,
  @Json(name = "datetime") val datetime: Long,
  @Json(name = "headline") val headline: String,
  @Json(name = "source") val source: String,
  @Json(name = "url") val url: String,
  @Json(name = "summary") val summary: String
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