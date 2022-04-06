package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.androidApp.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.stockprice.androidApp.domain.usecase.SearchRequestsUseCase
import com.ferelin.stockprice.androidApp.domain.entity.LceState
import com.ferelin.stockprice.androidApp.ui.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsStateUi(
  val isUserAuthenticated: Boolean = false,
  val downloadLce: LceState = LceState.None,
  val showDataCleared: Boolean = false,
  val showPathError: Boolean = false,
  val showNoPermissionsError: Boolean = false,
  val requestPermissions: Boolean = false,
  val requestStoragePath: Boolean = false
)

class SettingsViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val viewModelScope: CoroutineScope,
  private val dispatchersProvider: DispatchersProvider
) {
  private val viewModelState = MutableStateFlow(SettingsStateUi())
  val uiState = viewModelState.asStateFlow()

  // private val permissionsGranted = MutableStateFlow(permissionManager.writeExternalStorage)
  private val requestDownloadCode = MutableSharedFlow<Unit>()

  init {
    /*requestDownloadCode
      .combine(
        flow = permissionsGranted,
        transform = { _, isGranted ->
          if (!isGranted) viewModelState.update { it.copy(requestPermissions = true) }
          isGranted
        }
      )
      .filter { it }
      .combine(
        flow = storagePathUseCase.storagePath,
        transform = { _, storagePath ->
          if (!storagePath.isValid) viewModelState.update { it.copy(requestStoragePath = true) }
          storagePath
        }
      )
      .filter { it.isValid }
      .onEach(this::tryDownloadSourceCode)
      .launchIn(viewModelScope)

    downloadProjectUseCase.downloadLce
      .onEach(this::onDownloadLce)
      .launchIn(viewModelScope)

    authUserStateRepository.userAuthenticated
      .distinctUntilChanged()
      .onEach(this::onAuthenticate)
      .launchIn(viewModelScope)*/
  }

  fun onPermissions(isGranted: Boolean) {
    viewModelState.update {
      it.copy(
        requestPermissions = false,
        showNoPermissionsError = !isGranted
      )
    }
    // permissionsGranted.value = permissionManager.writeExternalStorage
  }

  fun onStoragePathSelected(path: String, authority: String) {
    viewModelState.update { it.copy(requestStoragePath = false) }
    /*viewModelScope.launch(dispatchersProvider.IO) {
      storagePathUseCase.setStoragePath(path, authority)
    }*/
  }

  fun onLogOutClick() {
    /*viewModelScope.launch(dispatchersProvider.IO) {
      authUseCase.logOut()
    }*/
  }

  fun onClearDataClick() {
    viewModelScope.launch(dispatchersProvider.IO) {
      searchRequestsUseCase.eraseAll()
      favouriteCompanyUseCase.eraseCache()
      viewModelState.update { it.copy(showDataCleared = true) }
    }
  }

  fun onDownloadCodeClick() {
    viewModelScope.launch {
      requestDownloadCode.emit(Unit)
    }
  }

  private fun onDownloadLce(lceState: LceState) {
    viewModelState.update { it.copy(downloadLce = lceState) }
  }

  private fun onAuthenticate(isAuthenticated: Boolean) {
    viewModelState.update { it.copy(isUserAuthenticated = isAuthenticated) }
  }

  /*private suspend fun tryDownloadSourceCode(storagePath: StoragePath) {
    viewModelScope.launch(dispatchersProvider.IO) {
      val destinationFile = storageManager.buildDownloadFile(
        treePath = storagePath.path,
        pathAuthority = storagePath.authority,
        fileName = DOWNLOAD_FILE_NAME
      )
      downloadProjectUseCase.download(destinationFile)
    }
  }*/
}
/*
internal val StoragePath.isValid: Boolean
  get() = this.path.isNotEmpty() && this.authority.isNotEmpty()*/

internal const val DOWNLOAD_FILE_NAME = "Stock-Price"