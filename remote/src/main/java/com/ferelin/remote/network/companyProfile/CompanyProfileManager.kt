package com.ferelin.remote.network.companyProfile

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompanyProfileManager(
    private val mOnResponse: (response: CompanyProfileResponse) -> Unit
) : Callback<CompanyProfileResponse> {

    override fun onResponse(
        call: Call<CompanyProfileResponse>,
        response: Response<CompanyProfileResponse>
    ) {
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            mOnResponse(responseBody)
        } else mOnResponse(CompanyProfileResponse.Fail(Throwable(response.message())))
    }

    override fun onFailure(call: Call<CompanyProfileResponse>, t: Throwable) {
        mOnResponse(CompanyProfileResponse.Fail(t))
    }

    companion object {
        val API = CompanyProfileApi::class.java
    }
}