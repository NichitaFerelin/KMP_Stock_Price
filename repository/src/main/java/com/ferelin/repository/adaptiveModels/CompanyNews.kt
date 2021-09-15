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

package com.ferelin.repository.adaptiveModels

class CompanyNews(
    var ids: List<String>,
    var headlines: List<String>,
    var summaries: List<String>,
    var sources: List<String>,
    var dates: List<String>,
    var browserUrls: List<String>,
    var previewImagesUrls: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is CompanyNews) {
            ids.firstOrNull() == other.ids.firstOrNull()
        } else false
    }

    override fun hashCode(): Int {
        return ids.firstOrNull().hashCode()
    }
}