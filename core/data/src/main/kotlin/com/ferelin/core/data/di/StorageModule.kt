package com.ferelin.core.data.di

import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.entity.company.CompanyJsonSourceImpl
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.entity.crypto.CryptoJsonSourceImpl
import com.ferelin.core.data.storage.AppDatabase
import com.ferelin.core.data.storage.PreferencesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
  single { AppDatabase.buildDatabase(androidContext()) }
  single { PreferencesProvider(get()) }

  factory { get<AppDatabase>().companyDao() }
  factory { get<AppDatabase>().newsDao() }
  factory { get<AppDatabase>().cryptoDao() }
  factory { get<AppDatabase>().cryptoPriceDao() }
  factory { get<AppDatabase>().favouriteCompanyDao() }
  factory { get<AppDatabase>().pastPriceDao() }
  factory { get<AppDatabase>().profileDao() }
  factory { get<AppDatabase>().searchRequestDao() }
  factory { get<AppDatabase>().stockPriceDao() }

  factory<CompanyJsonSource> { CompanyJsonSourceImpl(get(), get()) }
  factory<CryptoJsonSource> { CryptoJsonSourceImpl(get(), get()) }
}