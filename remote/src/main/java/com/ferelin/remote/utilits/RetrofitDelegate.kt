package com.ferelin.remote.utilits

import android.os.SystemClock
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.reflect.KProperty

class RetrofitDelegate(private val mUrl: String) {

    private var mRetrofit: Retrofit? = null
    private val mPerSecondRequestLimit = 1000L

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Retrofit {
        if (mRetrofit == null) {
            mRetrofit = buildRetrofit(mUrl)
        }
        return mRetrofit!!
    }

    private fun buildRetrofit(url: String): Retrofit {

        val rateLimitInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request())
                SystemClock.sleep(mPerSecondRequestLimit)
                return response
            }
        }

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(rateLimitInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}