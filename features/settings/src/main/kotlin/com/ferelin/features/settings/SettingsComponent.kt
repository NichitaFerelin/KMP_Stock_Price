package com.ferelin.features.settings

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.*
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SettingsScope

@SettingsScope
@Component(dependencies = [SettingsDeps::class])
internal interface SettingsComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: SettingsDeps): Builder
    fun build(): SettingsComponent
  }

  fun viewModelFactory(): SettingsViewModelFactory
}

interface SettingsDeps {
  val permissionManager: PermissionManager
  val storageManager: AppStorageManager
  val authUserStateRepository: AuthUserStateRepository
  val notifyPriceUseCase: NotifyPriceUseCase
  val storagePathUseCase: StoragePathUseCase
  val downloadProjectUseCase: DownloadProjectUseCase
  val searchRequestsUseCase: SearchRequestsUseCase
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val dispatchersProvider: DispatchersProvider
  val authUseCase: AuthUseCase
}