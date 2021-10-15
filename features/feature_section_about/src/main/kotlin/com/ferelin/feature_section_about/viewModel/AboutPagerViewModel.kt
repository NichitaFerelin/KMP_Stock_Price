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

package com.ferelin.feature_section_about.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.params.AboutParams
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AboutPagerViewModel @Inject constructor(
    private val mCompaniesInteractor: CompaniesInteractor,
    private val mStockStyleProvider: StockStyleProvider,
    private val mNetworkResolver: NetworkResolver,
    private val mRouter: Router,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel(), NetworkListener {

    private val mNetworkState = MutableSharedFlow<Boolean>()
    val networkState: SharedFlow<Boolean>
        get() = mNetworkState.asSharedFlow()

    init {
        mNetworkResolver.registerNetworkListener(this)
    }

    override suspend fun onNetworkAvailable() {
        mNetworkState.emit(true)
    }

    override suspend fun onNetworkLost() {
        mNetworkState.emit(false)
    }

    override fun onCleared() {
        mNetworkResolver.unregisterNetworkListener(this)
        super.onCleared()
    }

    var aboutParams = AboutParams()

    var isFavourite = false
        set(value) {
            field = value
            aboutParams.isFavourite = field
            favouriteIconRes = mStockStyleProvider.getForegroundIconDrawable(value)
        }

    var favouriteIconRes = mStockStyleProvider.getForegroundIconDrawable(isFavourite)

    val favouriteCompaniesUpdate: SharedFlow<Unit> by lazy(LazyThreadSafetyMode.NONE) {
        mCompaniesInteractor
            .observeFavouriteCompaniesUpdates()
            .filter { it.company.id == aboutParams.companyId }
            .onEach { isFavourite = it.company.isFavourite }
            .map { Unit }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))
    }

    fun onFavouriteIconClick() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            if (isFavourite) {
                mCompaniesInteractor.removeCompanyFromFavourites(aboutParams.companyId)
            } else {
                mCompaniesInteractor.addCompanyToFavourites(aboutParams.companyId)
            }
        }
    }

    fun onBackBtnClick() {
        mRouter.back()
    }
}