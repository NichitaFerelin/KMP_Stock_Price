package com.ferelin.core.data.repository

import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.ferelin.core.domain.repository.ProjectRepository
import java.io.File

internal class ProjectRepositoryImpl(
  private val downloadManager: DownloadManager
) : ProjectRepository {
  override suspend fun download(
    resultFileName: String,
    destinationFile: File?
  ) {
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