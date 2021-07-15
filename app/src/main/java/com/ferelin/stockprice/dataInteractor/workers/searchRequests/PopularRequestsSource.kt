package com.ferelin.stockprice.dataInteractor.workers.searchRequests

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

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

object PopularRequestsSource {

    /*
    * Mocked data
    * */
    val popularSearchRequests: List<AdaptiveSearchRequest> = listOf(
        AdaptiveSearchRequest("Apple"),
        AdaptiveSearchRequest("Microsoft Corp"),
        AdaptiveSearchRequest("Amazon.com"),
        AdaptiveSearchRequest("Alphabet"),
        AdaptiveSearchRequest("JD.com"),
        AdaptiveSearchRequest("Tesla"),
        AdaptiveSearchRequest("Facebook"),
        AdaptiveSearchRequest("Telefonaktiebolaget"),
        AdaptiveSearchRequest("NVIDIA"),
        AdaptiveSearchRequest("Beigene"),
        AdaptiveSearchRequest("Intel"),
        AdaptiveSearchRequest("Netflix"),
        AdaptiveSearchRequest("Adobe"),
        AdaptiveSearchRequest("Cisco"),
        AdaptiveSearchRequest("Yandex"),
        AdaptiveSearchRequest("Zoom"),
        AdaptiveSearchRequest("Starbucks"),
        AdaptiveSearchRequest("Charter"),
        AdaptiveSearchRequest("Sanofi"),
        AdaptiveSearchRequest("Amgen"),
        AdaptiveSearchRequest("Pepsi")
    )
}