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

package com.ferelin.stockprice.dataInteractor.workers.searchRequests

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

object PopularRequestsSource {

    /*
    * Mocked data
    * */
    val popularSearchRequests: List<AdaptiveSearchRequest> = listOf(
        AdaptiveSearchRequest(0, "Apple"),
        AdaptiveSearchRequest(1, "Microsoft Corp"),
        AdaptiveSearchRequest(2, "Amazon.com"),
        AdaptiveSearchRequest(3, "Alphabet"),
        AdaptiveSearchRequest(4, "JD.com"),
        AdaptiveSearchRequest(5, "Tesla"),
        AdaptiveSearchRequest(6, "Facebook"),
        AdaptiveSearchRequest(7, "Telefonaktiebolaget"),
        AdaptiveSearchRequest(8, "NVIDIA"),
        AdaptiveSearchRequest(9, "Beigene"),
        AdaptiveSearchRequest(10, "Intel"),
        AdaptiveSearchRequest(11, "Netflix"),
        AdaptiveSearchRequest(12, "Adobe"),
        AdaptiveSearchRequest(13, "Cisco"),
        AdaptiveSearchRequest(14, "Yandex"),
        AdaptiveSearchRequest(15, "Zoom"),
        AdaptiveSearchRequest(16, "Starbucks"),
        AdaptiveSearchRequest(17, "Charter"),
        AdaptiveSearchRequest(18, "Sanofi"),
        AdaptiveSearchRequest(19, "Amgen"),
        AdaptiveSearchRequest(20, "Pepsi")
    )
}