package com.ferelin.features.settings.ui

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.DownloadProjectUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.NotifyPriceUseCase
import com.ferelin.core.domain.usecase.StoragePathUseCase
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SettingsScope

@SettingsScope
@Component(dependencies = [SettingsDeps::class])
internal interface SettingsComponent {
  fun inject(settingsFragment: SettingsFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: SettingsDeps): Builder
    fun build(): SettingsComponent
  }
}

interface SettingsDeps {
  val coordinator: Coordinator
  val permissionManager: PermissionManager
  val storageManager: AppStorageManager
  val authUserStateRepository: AuthUserStateRepository
  val notifyPriceUseCase: NotifyPriceUseCase
  val storagePathUseCase: StoragePathUseCase
  val downloadProjectUseCase: DownloadProjectUseCase
}

interface SettingsDepsProvider {
  var deps: SettingsDeps

  companion object : SettingsDepsProvider by SettingsDepsStore
}

object SettingsDepsStore : SettingsDepsProvider {
  override var deps: SettingsDeps by Delegates.notNull()
}

internal class SettingsComponentViewModel : ViewModel() {
  val settingsComponent = DaggerSettingsComponent.builder()
    .dependencies(SettingsDepsStore.deps)
    .build()
}