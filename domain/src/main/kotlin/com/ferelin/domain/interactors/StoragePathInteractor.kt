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

package com.ferelin.domain.interactors

import com.ferelin.domain.repositories.StoragePathRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class StoragePathInteractor @Inject constructor(
    private val storagePathRepo: StoragePathRepo,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope,
) {
    companion object {
        const val SOURCE_CODE_FILE_NAME = "Stock-Price-Project"
    }

    /**
     * Get user selected storage path by which files can be saved
     * @return path if exists
     * */
    suspend fun getSelectedStoragePath(): String? {
        return storagePathRepo.getSelectedStoragePath()
    }

    /**
     * Get user storage path authority by which path Uri can be created
     * @return authority if exists
     * */
    suspend fun getStoragePathAuthority(): String? {
        return storagePathRepo.getStoragePathAuthority()
    }

    /**
     * Set user selected storage path
     * @param storagePath is a path selected by user
     * */
    suspend fun setSelectedStoragePath(storagePath: String) {
        externalScope.launch(dispatchersProvider.IO) {
            storagePathRepo.setSelectedStoragePath(storagePath)
        }
    }

    /**
     * Set storage authority for path
     * @param authority is an authority for path
     * */
    suspend fun setStoragePathAuthority(authority: String) {
        externalScope.launch(dispatchersProvider.IO) {
            storagePathRepo.setStoragePathAuthority(authority)
        }
    }
}