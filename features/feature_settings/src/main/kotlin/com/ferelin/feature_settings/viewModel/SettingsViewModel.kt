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
import com.ferelin.core.adapter.options.createOptionsAdapter
import com.ferelin.core.resolvers.LocalFilesResolver
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.resolvers.NotificationsResolver
import com.ferelin.core.utils.MenuOptionsProvider
import com.ferelin.core.utils.OptionType
import com.ferelin.core.viewData.OptionViewData
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.domain.interactors.StoragePathInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.domain.useCases.DownloadProjectUseCase
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

    DOWNLOAD_PATH_ERROR,
    DOWNLOAD_STARTING,
    DOWNLOAD_WILL_BE_STARTED,

    ASK_FOR_PATH
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
    private val menuOptionsProvider: MenuOptionsProvider
) : ViewModel() {

    private val _messageEvent = MutableSharedFlow<SettingsEvent>()
    val messageSettingsEvent: SharedFlow<SettingsEvent> = _messageEvent.asSharedFlow()

    val optionsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createOptionsAdapter(this::onOptionClick)
        ).apply { setHasStableIds(true) }
    }

    init {
        loadOptions()
    }

    fun onBackClick() {
        router.back()
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
                OptionType.SOURCE_CODE -> onSourceClick()
            }
        }
    }

    private suspend fun onAuthClick() {
        if (authenticationInteractor.isUserAuthenticated()) {
            authenticationInteractor.logOut()

            val updatedMenuOptions = menuOptionsProvider.buildMenuOptions(false)

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

    private suspend fun onSourceClick() {
        val storagePath = storagePathInteractor.getSelectedStoragePath()
        val pathAuthority = storagePathInteractor.getStoragePathAuthority()

        if (storagePath == null || pathAuthority == null) {
            _messageEvent.emit(SettingsEvent.ASK_FOR_PATH)
        } else {
            initSourceProjectDownload(storagePath, pathAuthority)
        }
    }

    private fun loadOptions() {
        viewModelScope.launch {
            val isUserAuth = authenticationInteractor.isUserAuthenticated()
            val menuOptions = menuOptionsProvider.buildMenuOptions(isUserAuth)

            withContext(Dispatchers.Main) {
                optionsAdapter.setData(menuOptions)
            }
        }
    }

    private suspend fun initSourceProjectDownload(storagePath: String, pathAuthority: String) {
        val destinationFile = localFilesResolver.buildDocumentFile(storagePath, pathAuthority)

        if (destinationFile != null) {
            val path = localFilesResolver.buildFilePath(destinationFile) +
                    "/${StoragePathInteractor.SOURCE_CODE_FILE_NAME}.zip"
            val resultFile = File(path)

            if (networkResolver.isNetworkAvailable) {
                _messageEvent.emit(SettingsEvent.DOWNLOAD_STARTING)
            } else {
                _messageEvent.emit(SettingsEvent.DOWNLOAD_WILL_BE_STARTED)
            }

            downloadProjectUseCase.download(
                resultFile,
                notificationsResolver.downloadTitle,
                notificationsResolver.downloadDescription
            )
        } else {
            _messageEvent.emit(SettingsEvent.DOWNLOAD_PATH_ERROR)
        }
    }
}