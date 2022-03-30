package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface CryptoPriceRepository {
  val cryptoPrices: Observable<List<CryptoPrice>>
  fun fetchPriceFor(cryptos: List<Crypto>): Completable
}