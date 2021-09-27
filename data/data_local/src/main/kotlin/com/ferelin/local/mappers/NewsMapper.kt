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

package com.ferelin.local.mappers

import com.ferelin.domain.entities.News
import com.ferelin.local.entities.NewsDBO
import javax.inject.Inject

class NewsMapper @Inject constructor() {

    fun map(news: News): NewsDBO {
        return NewsDBO(
            id = news.id,
            relationId = news.relationId,
            cloudId = news.cloudId,
            headline = news.headline,
            date = news.date,
            previewImageUrl = news.previewImageUrl,
            source = news.source,
            sourceUrl = news.sourceUrl,
            summary = news.summary
        )
    }

    fun map(dbo: NewsDBO): News {
        return News(
            id = dbo.id,
            relationId = dbo.relationId,
            cloudId = dbo.cloudId,
            headline = dbo.headline,
            date = dbo.date,
            previewImageUrl = dbo.previewImageUrl,
            source = dbo.source,
            sourceUrl = dbo.sourceUrl,
            summary = dbo.summary
        )
    }
}