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

package com.ferelin.data_local.mappers

import com.ferelin.domain.entities.News
import com.ferelin.data_local.entities.NewsDBO
import javax.inject.Inject

class NewsMapper @Inject constructor() {

    fun map(news: News): NewsDBO {
        return NewsDBO(
            id = news.id,
            relationCompanyId = news.relationCompanyId,
            cloudId = news.cloudId,
            headline = news.headline,
            date = news.date,
            previewImageUrl = news.previewImageUrl,
            source = news.source,
            sourceUrl = news.sourceUrl,
            summary = news.summary
        )
    }

    fun map(newsDBO: NewsDBO): News {
        return News(
            id = newsDBO.id,
            relationCompanyId = newsDBO.relationCompanyId,
            cloudId = newsDBO.cloudId,
            headline = newsDBO.headline,
            date = newsDBO.date,
            previewImageUrl = newsDBO.previewImageUrl,
            source = newsDBO.source,
            sourceUrl = newsDBO.sourceUrl,
            summary = newsDBO.summary
        )
    }
}