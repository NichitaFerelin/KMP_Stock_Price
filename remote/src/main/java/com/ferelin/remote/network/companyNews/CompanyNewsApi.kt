package com.ferelin.remote.network.companyNews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyNewsApi {
    // from=2020-04-30&to=2020-05-01
    @GET("company-news")
    fun getCompanyNews(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<CompanyNewsResponse>
}