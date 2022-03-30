package com.ferelin.core.domain.repository

import io.reactivex.rxjava3.core.Observable

interface StoragePathRepository {
  val path: Observable<String>
  val authority: Observable<String>
  fun setStoragePath(path: String, authority: String)
}