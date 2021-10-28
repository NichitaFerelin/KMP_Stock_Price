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

package com.ferelin.data_network_downloader.sources

import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import com.ferelin.domain.sources.ProjectSource
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ProjectSourceImpl @Inject constructor(
    private val downloadManager: DownloadManager
) : ProjectSource {

    companion object {
        private const val projectSourceUrl = "https://github.com/NikitaFerelin" +
                "/Android_Stock_Price/archive/refs/heads/master.zip"
    }

    override suspend fun download(
        destinationFile: File,
        downloadTitle: String,
        downloadDescription: String
    ) {
        Timber.d("download")

        val request = DownloadManager.Request(Uri.parse(projectSourceUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destinationFile))
            .setTitle(downloadTitle)
            .setDescription(downloadDescription)
            .setAllowedOverRoaming(true)
            .setAllowedOverMetered(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
        }

        downloadManager.enqueue(request)
    }
}