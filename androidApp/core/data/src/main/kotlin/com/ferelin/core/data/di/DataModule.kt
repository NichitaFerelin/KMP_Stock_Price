package com.ferelin.core.data.di

import android.app.DownloadManager
import android.content.Context
import com.ferelin.core.data.entity.FavouriteCompanyApi
import com.ferelin.core.data.entity.FavouriteCompanyApiImpl
import com.ferelin.core.data.repository.ProjectRepositoryImpl
import com.ferelin.core.data.repository.StoragePathRepositoryImpl
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.core.domain.repository.ProjectRepository
import com.ferelin.core.domain.repository.StoragePathRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nativeDataModule = module {
  single { PreferencesProvider(get()) }

  factory { androidContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }

  factory<FavouriteCompanyApi> { FavouriteCompanyApiImpl(get()) }
  factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
  factory<StoragePathRepository> { StoragePathRepositoryImpl(get()) }
}