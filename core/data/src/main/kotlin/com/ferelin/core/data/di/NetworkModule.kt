package com.ferelin.core.data.di

import android.content.Context
import com.ferelin.core.data.R
import com.ferelin.core.data.api.buildKtorHttpClient
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApiImpl
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApiImpl
import com.ferelin.core.data.entity.news.NewsApi
import com.ferelin.core.data.entity.news.NewsApiImpl
import com.ferelin.core.data.entity.pastPrice.PastPriceApi
import com.ferelin.core.data.entity.pastPrice.PastPriceApiImpl
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceApiImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.ktor.client.*
import javax.inject.Singleton

@Module(includes = [NetworkModuleBinds::class])
class NetworkModule {
  @Provides
  @Singleton
  fun ktorClient(): HttpClient {
    return buildKtorHttpClient()
  }

  @Provides
  @StocksToken
  fun stocksToken(context: Context): String {
    return context.resources.getString(R.string.api_finnhub_token)
  }

  @Provides
  @CryptosToken
  fun cryptosToken(context: Context): String {
    return context.resources.getString(R.string.api_nomics_token)
  }

  @Provides
  @Reusable
  internal fun moshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @Provides
  @Singleton
  internal fun firebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance()
      .apply { useAppLanguage() }
  }

  @Provides
  @Singleton
  internal fun firebaseReference(): DatabaseReference {
    return FirebaseDatabase.getInstance().reference
  }
}

@Suppress("unused")
@Module
internal interface NetworkModuleBinds {
  @Binds
  fun favouriteCompanyApi(impl: FavouriteCompanyApiImpl): FavouriteCompanyApi

  @Binds
  fun cryptoPriceApi(impl: CryptoPriceApiImpl): CryptoPriceApi

  @Binds
  fun newsApi(impl: NewsApiImpl): NewsApi

  @Binds
  fun pastPriceApi(impl: PastPriceApiImpl): PastPriceApi

  @Binds
  fun stockPriceApi(impl: StockPriceApiImpl): StockPriceApi
}