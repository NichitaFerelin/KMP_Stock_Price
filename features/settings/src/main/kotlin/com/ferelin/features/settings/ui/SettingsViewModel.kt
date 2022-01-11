package com.ferelin.features.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.*
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
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val authUseCase: AuthUseCase,
  private val dispatchersProvider: DispatchersProvider,
  authUserStateRepository: AuthUserStateRepository
) : ViewModel() {

  private val _event = MutableSharedFlow<SettingsEvent>()
  val event: Flow<SettingsEvent> = _event.asSharedFlow()

  private val permissionsGranted = MutableStateFlow(permissionManager.writeExternalStorage())
  private val requestDownloadCode = MutableSharedFlow<Unit>()

  val notifyPriceState = notifyPriceUseCase.notifyPriceState
  val downloadLce = downloadProjectUseCase.downloadLce
  val authState = authUserStateRepository.userAuthenticated.distinctUntilChanged()

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
        transform = { _, isGranted ->
          if (!isGranted) _event.emit(SettingsEvent.REQUEST_PERMISSIONS)
          isGranted
        }
      )
      .filter { it }
      .combine(
        flow = storagePathUseCase.storagePath,
        transform = { _, storagePath ->
          if (!storagePath.isValid) _event.emit(SettingsEvent.REQUEST_PATH)
          storagePath
        }
      )
      .filter { it.isValid }
      .onEach(this::tryDownloadSourceCode)
      .launchIn(viewModelScope)
  }

  fun onBack() {
    coordinator.onEvent(SettingsRouteEvent.BackRequested)
  }

  fun onPermissionsGranted() {
    permissionsGranted.value = permissionManager.writeExternalStorage()
  }

  fun onStoragePathSelected(path: String, authority: String) {
    viewModelScope.launch(dispatchersProvider.IO) {
      storagePathUseCase.setStoragePath(path, authority)
    }
  }

  private fun onOptionClick(settingsViewData: SettingsViewData) {
    viewModelScope.launch(dispatchersProvider.IO) {
      when (settingsViewData.type) {
        OptionType.LOG_OUT -> authUseCase.logOut()
        OptionType.AUTH -> coordinator.onEvent(SettingsRouteEvent.AuthenticationRequested)
        OptionType.CLEAR_DATA -> {
          searchRequestsUseCase.eraseAll()
          favouriteCompanyUseCase.eraseCache()
          _event.emit(SettingsEvent.DATA_CLEARED)
        }
        OptionType.SOURCE_CODE -> requestDownloadCode.emit(Unit)
        else -> Unit
      }
    }
  }

  private fun onSwitch(switchViewData: SwitchOptionViewData, isChecked: Boolean) {
    viewModelScope.launch(dispatchersProvider.IO) {
      when (switchViewData.type) {
        OptionType.NOTIFY_PRICE -> notifyPriceUseCase.setNotifyPrice(isChecked)
        else -> Unit
      }
    }
  }

  private suspend fun tryDownloadSourceCode(storagePath: StoragePath) {
    viewModelScope.launch(dispatchersProvider.IO) {
      val destinationFile = storageManager.buildDownloadFile(
        treePath = storagePath.path,
        pathAuthority = storagePath.authority,
        fileName = DOWNLOAD_FILE_NAME
      )
      downloadProjectUseCase.download(destinationFile)
    }
  }
}

internal val StoragePath.isValid: Boolean
  get() = this.path.isNotEmpty() && this.authority.isNotEmpty()

internal const val DOWNLOAD_FILE_NAME = "Stock-Price"