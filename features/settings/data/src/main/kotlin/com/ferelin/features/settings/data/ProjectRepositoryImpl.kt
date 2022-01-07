package com.ferelin.features.settings.data

import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.ferelin.core.checkBackgroundThread
import com.ferelin.features.settings.domain.repository.ProjectRepository
import java.io.File
import javax.inject.Inject

internal class ProjectRepositoryImpl @Inject constructor(
  private val downloadManager: DownloadManager
) : ProjectRepository {
  override suspend fun download(
    resultFileName: String,
    destinationFile: File?
  ) {
    checkBackgroundThread()
    val request = DownloadManager.Request(Uri.parse(PROJECT_SOURCE_URL))
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
      .setTitle(DOWNLOAD_TITLE)
      .setDescription(DOWNLOAD_DESCRIPTION)
      .setAllowedOverRoaming(true)
      .setAllowedOverMetered(true)

    if (destinationFile == null) {
      request.setDestinationInExternalPublicDir(
        Environment.DIRECTORY_DOWNLOADS,
        resultFileName
      )
    } else {
      request.setDestinationUri(Uri.fromFile(destinationFile))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      request.setRequiresCharging(false)
    }
    downloadManager.enqueue(request)
  }
}

internal const val PROJECT_SOURCE_URL = "https://github.com/NikitaFerelin" +
  "/Android_Stock_Price/archive/refs/heads/master.zip"
internal const val DOWNLOAD_TITLE = "Stock Price Download Manager"
internal const val DOWNLOAD_DESCRIPTION = "Downloading project source code"