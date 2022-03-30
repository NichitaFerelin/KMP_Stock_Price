package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.StoragePathRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

interface StoragePathUseCase {
  val storagePath: Observable<StoragePath>
  fun setStoragePath(path: String, authority: String)
}

internal class StoragePathUseCaseImpl @Inject constructor(
  private val storagePathRepository: StoragePathRepository
) : StoragePathUseCase {
  override val storagePath: Observable<StoragePath>
    get() = storagePathRepository.path.zipWith(
      storagePathRepository.authority
    ) { path, authority -> StoragePath(path, authority) }

  override fun setStoragePath(path: String, authority: String) {
    storagePathRepository.setStoragePath(path, authority)
  }
}