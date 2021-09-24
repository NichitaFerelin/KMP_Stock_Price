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
import com.ferelin.remote.utils.toDateStr

class NewsMapper {

    fun map(response: NewsResponse): News {
        return News(
            cloudId = response.id.toString().substringBefore('.'),
            headline = response.headline,
            date = response.dateTime.toLong().toDateStr(),
            previewImageUrl = response.previewImageUrl,
            source = response.source,
            sourceUrl = response.source,
            summary = response.summary
        )
    }
}