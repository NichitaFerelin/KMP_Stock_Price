package com.ferelin.stockprice.androidApp.domain.usecase

import com.ferelin.stockprice.androidApp.domain.entity.StoragePath
import com.ferelin.stockprice.androidApp.domain.repository.StoragePathRepository
import com.ferelin.stockprice.shared.ui.DispatchersProvider
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