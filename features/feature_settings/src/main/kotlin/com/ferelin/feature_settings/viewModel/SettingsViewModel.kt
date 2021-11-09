/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.feature_settings.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.resolvers.LocalFilesResolver
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.resolvers.NotificationsResolver
import com.ferelin.core.services.priceCheck.PriceCheckScheduler
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.domain.interactors.StoragePathInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.domain.useCases.DownloadProjectUseCase
import com.ferelin.domain.useCases.notifyPrice.NotifyPriceGetUseCase
import com.ferelin.domain.useCases.notifyPrice.NotifyPriceSetUseCase
import com.ferelin.feature_settings.adapter.createOptionsAdapter
import com.ferelin.feature_settings.adapter.createSwitchOptionAdapter
import com.ferelin.feature_settings.utils.MenuOptionsProvider
import com.ferelin.feature_settings.utils.OptionType
import com.ferelin.feature_settings.viewData.OptionViewData
import com.ferelin.feature_settings.viewData.SwitchOptionViewData
import com.ferelin.navigation.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

enum class SettingsEvent {
    LOG_OUT_COMPLETE,

    DATA_CLEARED,
    DATA_CLEARED_NO_NETWORK,

    DOWNLOAD_ERROR,
    DOWNLOAD_PATH_ERROR,
    DOWNLOAD_STARTING,
    DOWNLOAD_WILL_BE_STARTED,

    REQUEST_PATH,
    REQUEST_PERMISSIONS
}

class SettingsViewModel @Inject constructor(
    private val authenticationInteractor: AuthenticationInteractor,
    private val companiesInteractor: CompaniesInteractor,
    private val searchRequestsInteractor: SearchRequestsInteractor,
    private val storagePathInteractor: StoragePathInteractor,
    private val downloadProjectUseCase: DownloadProjectUseCase,
    private val networkResolver: NetworkResolver,
    private val notificationsResolver: NotificationsResolver,
    private val localFilesResolver: LocalFilesResolver,
    private val router: Router,
    private val menuOptionsProvider: MenuOptionsProvider,
    private val notifyPriceGetUseCase: NotifyPriceGetUseCase,
    private val notifyPriceSetUseCase: NotifyPriceSetUseCase,
    private val priceChekScheduler: PriceCheckScheduler
) : ViewModel() {

    private val _messageEvent = MutableSharedFlow<SettingsEvent>()
    val messageEvent: SharedFlow<SettingsEvent> = _messageEvent.asSharedFlow()

    private var shouldSendNotifications = false

    val optionsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createOptionsAdapter(this::onOptionClick),
            createSwitchOptionAdapter(this::onSwitched)
        ).apply { setHasStableIds(true) }
    }

    fun onBackClick() {
        router.back()
    }

    fun loadOptions() {
        viewModelScope.launch {
            val isUserAuth = authenticationInteractor.isUserAuthenticated()
            shouldSendNotifications = notifyPriceGetUseCase.get()

            val menuOptions =
                menuOptionsProvider.buildMenuOptions(isUserAuth, shouldSendNotifications)

            withContext(Dispatchers.Main) {
                optionsAdapter.setData(menuOptions)
            }
        }
    }

    fun onPermissionsGranted() {
        viewModelScope.launch {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                downloadProjectToDefaultStorage()
            } else {
                downloadProjectToUserStorage()
            }
        }
    }

    fun onPathSelected(uri: Uri?) {
        viewModelScope.launch {
            if (uri == null || uri.path == null || uri.authority == null) {
                _messageEvent.emit(SettingsEvent.DOWNLOAD_PATH_ERROR)
                return@launch
            }

            val path = uri.path!!
            val authority = uri.authority!!

            storagePathInteractor.setSelectedStoragePath(path)
            storagePathInteractor.setStoragePathAuthority(authority)

            initSourceProjectDownload(path, authority)
        }
    }

    private fun onOptionClick(optionViewData: OptionViewData) {
        viewModelScope.launch {
            when (optionViewData.type) {
                OptionType.AUTH -> onAuthClick()
                OptionType.CLEAR_DATA -> onClearClick()
                OptionType.SOURCE_CODE -> onDownloadProjectClick()
                else -> Unit
            }
        }
    }

    private fun onSwitched(switchOptionViewData: SwitchOptionViewData, isChecked: Boolean) {
        viewModelScope.launch {
            if (switchOptionViewData.type == OptionType.NOTIFY_PRICE) {
                onNotifyPriceSwitched(isChecked)
            }
        }
    }

    private suspend fun onAuthClick() {
        if (authenticationInteractor.isUserAuthenticated()) {
            authenticationInteractor.logOut()

            val updatedMenuOptions =
                menuOptionsProvider.buildMenuOptions(false, shouldSendNotifications)

            withContext(Dispatchers.Main) {
                optionsAdapter.setData(updatedMenuOptions)
            }
            _messageEvent.emit(SettingsEvent.LOG_OUT_COMPLETE)
        } else {
            router.fromSettingsToLogin()
        }
    }

    private suspend fun onClearClick() {
        companiesInteractor.eraseUserData()
        searchRequestsInteractor.eraseUserData()

        val event = if (
            networkResolver.isNetworkAvailable
            || !authenticationInteractor.isUserAuthenticated()
        ) {
            SettingsEvent.DATA_CLEARED
        } else {
            SettingsEvent.DATA_CLEARED_NO_NETWORK
        }
        _messageEvent.emit(event)
    }

    private suspend fun onDownloadProjectClick() {
        _messageEvent.emit(SettingsEvent.REQUEST_PERMISSIONS)
    }

    private suspend fun onNotifyPriceSwitched(shouldSendNotifications: Boolean) {
        this.shouldSendNotifications = shouldSendNotifications
        notifyPriceSetUseCase.set(shouldSendNotifications)

        if (shouldSendNotifications) {
            priceChekScheduler.schedule()
        } else {
            priceChekScheduler.cancel()
        }
    }

    private suspend fun downloadProjectToDefaultStorage() {
        try {
            notifyDownloading()

            downloadProjectUseCase.download(
                notificationsResolver.downloadTitle,
                notificationsResolver.downloadDescription,
                null,
                StoragePathInteractor.SOURCE_CODE_FILE_NAME
            )
        } catch (e: Exception) {
            _messageEvent.emit(SettingsEvent.DOWNLOAD_ERROR)
        }
    }

    private suspend fun downloadProjectToUserStorage() {
        val storagePath = storagePathInteractor.getSelectedStoragePath()
        val pathAuthority = storagePathInteractor.getStoragePathAuthority()

        if (storagePath == null || pathAuthority == null) {
            _messageEvent.emit(SettingsEvent.REQUEST_PATH)
        } else {
            initSourceProjectDownload(storagePath, pathAuthority)
        }
    }

    private suspend fun initSourceProjectDownload(storagePath: String, pathAuthority: String) {
        val destinationFile = localFilesResolver.buildDocumentFile(storagePath, pathAuthority)

        if (destinationFile != null) {
            val path = localFilesResolver.buildFilePath(destinationFile) +
                    StoragePathInteractor.SOURCE_CODE_FILE_NAME
            val resultFile = File(path)

            notifyDownloading()

            try {
                downloadProjectUseCase.download(
                    notificationsResolver.downloadTitle,
                    notificationsResolver.downloadDescription,
                    resultFile
                )
            } catch (exception: SecurityException) {
                _messageEvent.emit(SettingsEvent.DOWNLOAD_PATH_ERROR)
            } catch (e: Exception) {
                _messageEvent.emit(SettingsEvent.DOWNLOAD_ERROR)
            }
        } else {
            _messageEvent.emit(SettingsEvent.DOWNLOAD_PATH_ERROR)
        }
    }

    private suspend fun notifyDownloading() {
        if (networkResolver.isNetworkAvailable) {
            _messageEvent.emit(SettingsEvent.DOWNLOAD_STARTING)
        } else {
            _messageEvent.emit(SettingsEvent.DOWNLOAD_WILL_BE_STARTED)
        }
    }
}