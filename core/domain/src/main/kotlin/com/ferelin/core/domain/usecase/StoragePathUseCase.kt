package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.StoragePathRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

interface StoragePathUseCase {
  val storagePath: Flow<StoragePath>
  suspend fun setStoragePath(path: String, authority: String)
}

internal class StoragePathUseCaseImpl(
  private val storagePathRepository: StoragePathRepository,
  private val dispatchersProvider: DispatchersProvider
) : StoragePathUseCase {
  override val storagePath: Flow<StoragePath>
    get() = storagePathRepository.path
      .combine(
        flow = storagePathRepository.authority,
        transform = { path, authority ->
          StoragePath(path, authority)
        }
      )
      .flowOn(dispatchersProvider.IO)

  override suspend fun setStoragePath(path: String, authority: String) {
    storagePathRepository.setStoragePath(path, authority)
  }
}