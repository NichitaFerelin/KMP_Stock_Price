package com.ferelin.core.domain.repository

import io.reactivex.rxjava3.core.Completable
import java.io.File

interface ProjectRepository {
  fun download(
    resultFileName: String,
    destinationFile: File? = null
  ) : Completable
}