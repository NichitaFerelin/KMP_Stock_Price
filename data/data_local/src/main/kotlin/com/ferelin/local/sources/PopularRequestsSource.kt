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

package com.ferelin.local.sources

import com.ferelin.domain.entities.SearchRequest

object PopularRequestsSource {

    val popularSearchRequests = listOf(
        SearchRequest(0, "Apple"),
        SearchRequest(1, "Microsoft Corp"),
        SearchRequest(2, "Amazon.com"),
        SearchRequest(3, "Alphabet"),
        SearchRequest(4, "JD.com"),
        SearchRequest(5, "Tesla"),
        SearchRequest(6, "Facebook"),
        SearchRequest(7, "Telefonaktiebolaget"),
        SearchRequest(8, "NVIDIA"),
        SearchRequest(9, "Beigene"),
        SearchRequest(10, "Intel"),
        SearchRequest(11, "Netflix"),
        SearchRequest(12, "Adobe"),
        SearchRequest(13, "Cisco"),
        SearchRequest(14, "Yandex"),
        SearchRequest(15, "Zoom"),
        SearchRequest(16, "Starbucks"),
        SearchRequest(17, "Charter"),
        SearchRequest(18, "Sanofi"),
        SearchRequest(19, "Amgen"),
        SearchRequest(20, "Pepsi")
    )
}