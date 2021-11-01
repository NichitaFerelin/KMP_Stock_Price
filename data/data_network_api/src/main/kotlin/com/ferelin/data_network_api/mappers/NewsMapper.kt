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

package com.ferelin.data_network_api.mappers

import com.ferelin.data_network_api.entities.NewsResponse
import com.ferelin.data_network_api.utils.toBasicMillisTime
import com.ferelin.domain.entities.News
import javax.inject.Inject

class NewsMapper @Inject constructor() {

    fun map(newsResponse: NewsResponse, companyId: Int): News {
        return News(
            relationCompanyId = companyId,
            cloudId = newsResponse.id.toString(),
            headline = newsResponse.headline,
            dateMillis = newsResponse.dateTime.toLong().toBasicMillisTime(),
            previewImageUrl = newsResponse.previewImageUrl,
            source = newsResponse.source,
            sourceUrl = newsResponse.sourceUrl,
            summary = newsResponse.summary
        )
    }
}