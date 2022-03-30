package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.ProjectRepository
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

interface DownloadProjectUseCase {
  fun download(destinationFile: File? = null)
  val downloadLce: Flow<LceState>
}

@Reusable
internal class DownloadProjectUseCaseImpl @Inject constructor(
  private val projectRepository: ProjectRepository
) : DownloadProjectUseCase {
  override fun download(destinationFile: File?) {
    projectRepository
      .download(RESULT_FILE_NAME, destinationFile)
      .doOnSubscribe { downloadLceState.value = LceState.Loading }
      .doOnComplete { downloadLceState.value = LceState.Content }
      .doOnError { downloadLceState.value = LceState.Error(it.message) }
      .blockingAwait()
  }

  private val downloadLceState = MutableStateFlow<LceState>(LceState.None)
  override val downloadLce: Flow<LceState> = downloadLceState.asStateFlow()
}

internal const val RESULT_FILE_NAME = "Stock Price"