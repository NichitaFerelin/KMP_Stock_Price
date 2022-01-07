package com.ferelin.features.settings.domain

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.features.settings.domain.repository.NotifyPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface NotifyPriceUseCase {
  val notifyPriceState: Flow<Boolean>
  suspend fun setNotifyPrice(notify: Boolean)
}

internal class NotifyPriceUseCaseImpl @Inject constructor(
  private val notifyPriceRepository: NotifyPriceRepository,
  private val dispatchersProvider: DispatchersProvider
) : NotifyPriceUseCase {
  override val notifyPriceState: Flow<Boolean> = notifyPriceRepository.notifyPrice
    .distinctUntilChanged()
    .flowOn(dispatchersProvider.IO)

  override suspend fun setNotifyPrice(notify: Boolean) {
    notifyPriceRepository.setNotifyPrice(notify)
  }
}