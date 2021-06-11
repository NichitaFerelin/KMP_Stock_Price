package com.ferelin.local.json

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

import android.content.Context
import com.ferelin.local.models.Company
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class JsonManagerImpl @Inject constructor(
    private val mContext: Context
) : JsonManager {

    override fun getCompaniesFromJson(): List<Company> {
        return JsonAssetsReader(mContext, JsonAssets.COMPANIES).readCompanies()
    }
}