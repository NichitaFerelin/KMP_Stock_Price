package com.ferelin.core.data.di

import android.app.DownloadManager
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class DownloadManagerModule {
  @Provides
  fun downloadManager(context: Context) : DownloadManager {
    return context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
  }
}