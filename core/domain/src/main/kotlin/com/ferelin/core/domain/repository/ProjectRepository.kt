package com.ferelin.core.domain.repository

import java.io.File

interface ProjectRepository {
  suspend fun download(
    resultFileName: String,
    destinationFile: File? = null
  )
}