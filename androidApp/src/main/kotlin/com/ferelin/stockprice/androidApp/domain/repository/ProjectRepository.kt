package com.ferelin.stockprice.androidApp.domain.repository

import java.io.File

interface ProjectRepository {
  suspend fun download(
    resultFileName: String,
    destinationFile: File? = null
  )
}