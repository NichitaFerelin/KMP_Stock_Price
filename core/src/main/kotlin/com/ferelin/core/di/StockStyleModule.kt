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

package com.ferelin.core.di

import com.ferelin.core.R
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class StockStyleModule {

    @Provides
    @Named("BackIcon")
    fun provideBackIcon(): Int = R.drawable.ic_favourite

    @Provides
    @Named("BackIconActive")
    fun provideBackIconActive(): Int = R.drawable.ic_favourite_active

    @Provides
    @Named("ForeIcon")
    fun provideForeIcon(): Int = R.drawable.ic_star

    @Provides
    @Named("ForeIconActive")
    fun provideForeIconActive(): Int = R.drawable.ic_star_active

    @Provides
    @Named("RippleLight")
    fun provideRippleLight(): Int = R.drawable.ripple_light

    @Provides
    @Named("RippleDark")
    fun provideRippleDark(): Int = R.drawable.ripple_dark

    @Provides
    @Named("ProfitPlus")
    fun provideProfitPlus(): Int = R.color.green

    @Provides
    @Named("ProfitMinus")
    fun provideProfitMinus(): Int = R.color.red

    @Provides
    @Named("HolderFirst")
    fun provideHolderFirst(): Int = R.color.white

    @Provides
    @Named("HolderSecond")
    fun provideHolderSecond(): Int = R.color.whiteDark
}