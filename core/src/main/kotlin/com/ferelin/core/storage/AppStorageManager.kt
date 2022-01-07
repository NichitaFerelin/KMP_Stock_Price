package com.ferelin.core.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.ferelin.core.ApplicationContext
import java.io.File
import javax.inject.Inject

class AppStorageManager @Inject constructor(
  @ApplicationContext private val context: Context
) {
  fun buildDownloadFile(
    treePath: String,
    pathAuthority: String
  ): File? {
    return try {
      val uriByPath = Uri.Builder()
        .path(treePath)
        .authority(pathAuthority)
        .build()

      val docFile = DocumentFile.fromTreeUri(context, uriByPath)
      val filePath = buildFilePath(docFile!!)
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