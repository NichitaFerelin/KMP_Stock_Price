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

package com.ferelin.feature_stocks_default.viewModel

import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import javax.inject.Inject

class StocksViewModel @Inject constructor(
    stockMapper: StockMapper,
    router: Router,
    companiesInteractor: CompaniesInteractor,
    stockPriceInteractor: StockPriceInteractor,
    dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
    stockMapper,
    router,
    companiesInteractor,
    stockPriceInteractor,
    dispatchersProvider
)