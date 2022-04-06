package com.ferelin.stockprice.androidApp.domain.usecase

import com.ferelin.stockprice.androidApp.domain.repository.ProjectRepository
import com.ferelin.stockprice.shared.domain.entity.LceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

interface DownloadProjectUseCase {
  suspend fun download(destinationFile: File? = null)
  val downloadLce: Flow<LceState>
}

internal class DownloadProjectUseCaseImpl(
  private val projectRepository: ProjectRepository
) : DownloadProjectUseCase {
  override suspend fun download(destinationFile: File?) {
    try {
      downloadLceState.value = LceState.Loading
      projectRepository.download(RESULT_FILE_NAME, destinationFile)
      downloadLceState.value = LceState.Content
    } catch (e: Exception) {
      downloadLceState.value = LceState.Error(e.message)
    }
  }

  private val downloadLceState = MutableStateFlow<LceState>(LceState.None)
  override val downloadLce: Flow<LceState> = downloadLceState.asStateFlow()
}

internal const val RESULT_FILE_NAME = "Stock Price"