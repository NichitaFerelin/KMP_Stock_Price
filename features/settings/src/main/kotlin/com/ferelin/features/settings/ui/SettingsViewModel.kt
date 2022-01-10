package com.ferelin.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.DownloadProjectUseCase
import com.ferelin.core.domain.usecase.NotifyPriceUseCase
import com.ferelin.core.domain.usecase.StoragePathUseCase
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.routing.Coordinator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal enum class SettingsEvent {
  DATA_CLEARED,
  DATA_CLEARED_NO_NETWORK,
  PATH_ERROR,
  REQUEST_PATH,
  REQUEST_PERMISSIONS
}

internal class SettingsViewModel @Inject constructor(
  private val notifyPriceUseCase: NotifyPriceUseCase,
  private val storagePathUseCase: StoragePathUseCase,
  private val downloadProjectUseCase: DownloadProjectUseCase,
  private val permissionManager: PermissionManager,
  private val storageManager: AppStorageManager,
  private val coordinator: Coordinator,
  authUserStateRepository: AuthUserStateRepository
) : ViewModel() {

  private val _event = MutableSharedFlow<SettingsEvent>()
  val event: Flow<SettingsEvent> = _event.asSharedFlow()

  private val permissionsGranted = MutableStateFlow(permissionManager.writeExternalStorage())
  private val requestDownloadCode = MutableSharedFlow<Unit>()

  val notifyPriceState = notifyPriceUseCase.notifyPriceState
  val downloadLce = downloadProjectUseCase.downloadLce
  val authState = authUserStateRepository.userAuthenticated

  val optionsAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createOptionsAdapter(this::onOptionClick),
      createSwitchOptionAdapter(this::onSwitch)
    ).apply { setHasStableIds(true) }
  }

  init {
    requestDownloadCode
      .combine(
        flow = permissionsGranted,
        transform = { _, isGranted -> isGranted }
      )
      .filter { it }
      .combine(
        flow = storagePathUseCase.storagePath,
        transform = { _, storagePath -> storagePath }
      )
      .onEach(this::tryDownloadSourceCode)
      .launchIn(viewModelScope)
  }

  fun onBack() {
    coordinator.onEvent(SettingsRouteEvent.BackRequested)
  }

  fun onStoragePathSelected(path: String, authority: String) {
    viewModelScope.launch {
      storagePathUseCase.setStoragePath(path, authority)
    }
  }

  fun onPermissionsGranted() {
    permissionsGranted.value = permissionManager.writeExternalStorage()
  }

  private fun onOptionClick(settingsViewData: SettingsViewData) {
    viewModelScope.launch {
      when (settingsViewData.type) {
        OptionType.AUTH -> coordinator.onEvent(SettingsRouteEvent.AuthenticationRequested)
        OptionType.CLEAR_DATA -> Unit
        OptionType.SOURCE_CODE -> requestDownloadCode.emit(Unit)
        else -> Unit
      }
    }
  }

  private fun onSwitch(switchViewData: SwitchOptionViewData, isChecked: Boolean) {
    viewModelScope.launch {
      when (switchViewData.type) {
        OptionType.NOTIFY_PRICE -> notifyPriceUseCase.setNotifyPrice(isChecked)
        else -> Unit
      }
    }
  }

  private suspend fun tryDownloadSourceCode(storagePath: StoragePath) {
    when {
      !permissionManager.writeExternalStorage() -> {
        _event.emit(SettingsEvent.REQUEST_PERMISSIONS)
      }
      storagePath.path.isEmpty() || storagePath.authority.isEmpty() -> {
        _event.emit(SettingsEvent.REQUEST_PATH)
      }
      else -> {
        val destinationFile = storageManager.buildDownloadFile(
          treePath = storagePath.path,
          pathAuthority = storagePath.authority
        )
        downloadProjectUseCase.download(destinationFile)
      }
    }
  }
}