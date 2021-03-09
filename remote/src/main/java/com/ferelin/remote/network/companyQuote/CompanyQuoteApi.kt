package com.ferelin.remote.network.companyQuote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyQuoteApi {

    @GET("quote")
    fun getCompanyQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<CompanyQuoteResponse>
}