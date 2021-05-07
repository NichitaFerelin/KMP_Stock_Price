package com.ferelin.stockprice.dataInteractor.local

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

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

sealed class LocalInteractorResponse {
    data class Success(
        val companies: List<AdaptiveCompany> = emptyList(),
        val searchesHistory: List<AdaptiveSearchRequest> = emptyList(),
        val firstTimeLaunch: Boolean = false
    ) : LocalInteractorResponse()

    data class Failed(val error: String? = null) : LocalInteractorResponse()
}
