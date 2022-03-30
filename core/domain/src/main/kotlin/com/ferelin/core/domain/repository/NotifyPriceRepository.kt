package com.ferelin.core.domain.repository

import io.reactivex.rxjava3.core.Observable

interface NotifyPriceRepository {
  val notifyPrice: Observable<Boolean>
  fun setNotifyPrice(notify: Boolean)
}