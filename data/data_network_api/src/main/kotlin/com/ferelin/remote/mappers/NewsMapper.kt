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

package com.ferelin.remote.mappers

import com.ferelin.domain.entities.News
import com.ferelin.remote.entities.NewsResponse
import com.ferelin.remote.utils.toBasicMillisTime
import com.ferelin.remote.utils.toDateStr
import javax.inject.Inject

class NewsMapper @Inject constructor() {

    fun map(newsResponse: NewsResponse, companyId: Int): News {
        return News(
            relationCompanyId = companyId,
            cloudId = newsResponse.id.toString().substringBefore('.'),
            headline = newsResponse.headline,
            date = newsResponse.dateTime.toLong().toBasicMillisTime().toDateStr(),
            previewImageUrl = newsResponse.previewImageUrl,
            source = newsResponse.source,
            sourceUrl = newsResponse.sourceUrl,
            summary = newsResponse.summary
        )
    }
}