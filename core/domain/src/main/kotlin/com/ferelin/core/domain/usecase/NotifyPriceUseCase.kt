package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.repository.NotifyPriceRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

interface NotifyPriceUseCase {
  val notifyPriceState: Observable<Boolean>
  fun setNotifyPrice(notify: Boolean)
}

@Reusable
internal class NotifyPriceUseCaseImpl @Inject constructor(
  private val notifyPriceRepository: NotifyPriceRepository
) : NotifyPriceUseCase {
  override val notifyPriceState: Observable<Boolean> = notifyPriceRepository.notifyPrice

  override fun setNotifyPrice(notify: Boolean) {
    notifyPriceRepository.setNotifyPrice(notify)
  }
}