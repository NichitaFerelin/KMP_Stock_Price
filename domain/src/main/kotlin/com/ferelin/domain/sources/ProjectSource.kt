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

package com.ferelin.domain.sources

import java.io.File

interface ProjectSource {

    /**
     * Loads source project code from github.
     * Download can be done by File object or using default device storage. In this
     * case [resultFileName] is required
     * @param downloadTitle is a title of download-notification
     * @param downloadDescription is a description of download-notification
     * @param destinationFile is a document file to start .zip downloading
     * @param resultFileName is a result file name
     * */
    suspend fun download(
        downloadTitle: String,
        downloadDescription: String,
        destinationFile: File? = null,
        resultFileName: String? = null
    )
}