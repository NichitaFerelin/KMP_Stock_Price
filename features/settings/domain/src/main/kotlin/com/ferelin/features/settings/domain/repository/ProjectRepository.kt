package com.ferelin.features.settings.domain.repository

import java.io.File

interface ProjectRepository {
  suspend fun download(
    resultFileName: String,
    destinationFile: File? = null
  )
}