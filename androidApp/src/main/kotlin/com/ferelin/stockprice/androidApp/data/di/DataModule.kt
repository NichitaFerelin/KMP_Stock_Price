package com.ferelin.stockprice.androidApp.data.di

import android.app.DownloadManager
import android.content.Context
import com.ferelin.stockprice.androidApp.data.entity.FavouriteCompanyApi
import com.ferelin.stockprice.androidApp.data.entity.FavouriteCompanyApiImpl
import com.ferelin.stockprice.androidApp.data.repository.ProjectRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.StoragePathRepositoryImpl
import com.ferelin.stockprice.androidApp.data.storage.PreferencesProvider
import com.ferelin.stockprice.androidApp.domain.repository.ProjectRepository
import com.ferelin.stockprice.androidApp.domain.repository.StoragePathRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nativeDataModule = module {
  single { PreferencesProvider(get()) }

  factory { androidContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }

  factory<FavouriteCompanyApi> { FavouriteCompanyApiImpl(get()) }
  factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
  factory<StoragePathRepository> { StoragePathRepositoryImpl(get()) }
}