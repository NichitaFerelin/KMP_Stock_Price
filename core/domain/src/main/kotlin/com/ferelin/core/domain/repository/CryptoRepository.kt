package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Crypto
import io.reactivex.rxjava3.core.Observable

interface CryptoRepository {
  val cryptos: Observable<List<Crypto>>
}