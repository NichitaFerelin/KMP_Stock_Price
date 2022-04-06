package com.ferelin.core.di

import com.ferelin.core.storage.StoragePathBuilder
import org.koin.dsl.module

val storagePathBuilderModule = module {
  factory { StoragePathBuilder(get()) }
}