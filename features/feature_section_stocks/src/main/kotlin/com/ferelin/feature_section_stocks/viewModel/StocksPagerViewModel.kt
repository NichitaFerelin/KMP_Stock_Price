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

package com.ferelin.feature_section_stocks.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.domain.useCases.crypto.GetCryptoPriceUseCase
import com.ferelin.domain.useCases.crypto.LoadCryptoUseCase
import com.ferelin.feature_section_stocks.adapter.crypto.createCryptoAdapter
import com.ferelin.feature_section_stocks.mapper.CryptoWithPriceMapper
import com.ferelin.navigation.Router
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StocksPagerViewModel @Inject constructor(
    private val router: Router,
    private val getCryptoPriceUseCase: GetCryptoPriceUseCase,
    private val loadCryptoUseCase: LoadCryptoUseCase,
    private val cryptoWithPriceMapper: CryptoWithPriceMapper,
    private val networkResolver: NetworkResolver
) : ViewModel(), NetworkListener {

    private var cryptoLoaded = false

    private val _cryptoLoading = MutableStateFlow(false)
    val cryptoLoading: StateFlow<Boolean> = _cryptoLoading.asStateFlow()

    val cryptoAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createCryptoAdapter()
        ).apply { setHasStableIds(true) }
    }

    var lastSelectedPage = 0

    init {
        loadCrypto()
        networkResolver.registerNetworkListener(this)
    }

    override suspend fun onNetworkAvailable() {
        if (!cryptoLoaded) {
            loadCrypto()
        }
    }

    override suspend fun onNetworkLost() {
        // Do nothing
    }

    override fun onCleared() {
        networkResolver.unregisterNetworkListener(this)
        super.onCleared()
    }

    fun onSettingsClick() {
        router.fromStocksPagerToSettings()
    }

    fun onSearchCardClick(sharedElement: View, name: String) {
        router.fromStocksPagerToSearch { fragmentTransaction ->
            fragmentTransaction.addSharedElement(sharedElement, name)
        }
    }

    private fun loadCrypto() {
        viewModelScope.launch {
            _cryptoLoading.value = true

            val localCryptoPrices = getCryptoPriceUseCase.getAll()
            val loaded = loadCryptoUseCase.loadInto(localCryptoPrices)

            if (loaded) {
                cryptoLoaded = true
            }

            _cryptoLoading.value = false

            withContext(Dispatchers.Main) {
                cryptoAdapter.setData(
                    data = localCryptoPrices.map(cryptoWithPriceMapper::map)
                )
            }
        }
    }
}