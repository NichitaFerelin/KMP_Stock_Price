package com.ferelin.core.data.di

import android.app.DownloadManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val downloadManagerModule = module {
  factory { androidContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
}