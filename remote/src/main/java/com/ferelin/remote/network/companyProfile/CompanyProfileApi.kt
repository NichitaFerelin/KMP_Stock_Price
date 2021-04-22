package com.ferelin.remote.network.companyProfile

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyProfileApi {
    @GET("stock/profile2")
    fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<CompanyProfileResponse>
}