package com.ferelin.core.di

import com.ferelin.core.permission.PermissionManager
import org.koin.dsl.module

val permissionModule = module {
  factory { PermissionManager(get()) }
}