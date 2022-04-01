package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.NotifyPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

interface NotifyPriceUseCase {
  val notifyPriceState: Flow<Boolean>
  suspend fun setNotifyPrice(notify: Boolean)
}

internal class NotifyPriceUseCaseImpl(
  private val notifyPriceRepository: NotifyPriceRepository,
  dispatchersProvider: DispatchersProvider
) : NotifyPriceUseCase {
  override val notifyPriceState: Flow<Boolean> = notifyPriceRepository.notifyPrice
    .distinctUntilChanged()
    .flowOn(dispatchersProvider.IO)

  override suspend fun setNotifyPrice(notify: Boolean) {
    notifyPriceRepository.setNotifyPrice(notify)
  }
}