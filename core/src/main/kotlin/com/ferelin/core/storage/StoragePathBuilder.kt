package com.ferelin.core.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

class StoragePathBuilder(
  private val context: Context
) {
  fun buildDownloadFile(
    treePath: String,
    pathAuthority: String,
    fileName: String
  ): File? {
    return try {
      val uriByPath = Uri.Builder()
        .path(treePath)
        .authority(pathAuthority)
        .build()

      val docFile = DocumentFile.fromTreeUri(context, uriByPath)
      val filePath = buildFilePath(docFile!!) + "/$fileName.zip"
      File(filePath)
    } catch (e: Exception) {
      null
    }
  }

  private fun buildFilePath(file: DocumentFile): String {
    var finalPath = "${file.name}"
    var parentFile = file.parentFile

    while (parentFile != null) {
      finalPath = "${parentFile.name}/$finalPath"
      parentFile = parentFile.parentFile
    }
    return "sdcard/$finalPath"
  }
}