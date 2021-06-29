package com.ferelin.stockprice.viewModelFactories

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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.aboutSection.aboutSection.AboutPagerViewModel
import com.ferelin.stockprice.ui.aboutSection.chart.ChartViewModel
import com.ferelin.stockprice.ui.aboutSection.news.NewsViewModel
import com.ferelin.stockprice.ui.aboutSection.profile.ProfileViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
open class CompanyViewModelFactory @Inject constructor(
    private val mSelectedCompany: AdaptiveCompany?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChartViewModel::class.java) -> {
                ChartViewModel(mSelectedCompany!!) as T
            }
            modelClass.isAssignableFrom(AboutPagerViewModel::class.java) -> {
                AboutPagerViewModel(mSelectedCompany!!) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(mSelectedCompany!!) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(mSelectedCompany!!) as T
            }
            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}