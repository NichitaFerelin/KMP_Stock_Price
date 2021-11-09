/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.domain.useCases.notifyPrice

import com.ferelin.domain.repositories.NotifyPriceRepo
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class NotifyPriceSetUseCase @Inject constructor(
    private val notifyPriceRepo: NotifyPriceRepo,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    suspend fun set(notify: Boolean) {
        externalScope.launch {
            notifyPriceRepo.set(notify)
        }
    }
}