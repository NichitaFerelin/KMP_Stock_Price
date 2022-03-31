package com.ferelin.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.*
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import dagger.Component
import javax.inject.Scope

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

internal class SettingsComponentViewModel(deps: SettingsDeps) : ViewModel() {
  val component = DaggerSettingsComponent.builder()
    .dependencies(deps)
    .build()
}

internal class SettingsComponentViewModelFactory(
  private val deps: SettingsDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == SettingsComponentViewModel::class.java)
    return SettingsComponentViewModel(deps) as T
  }
}