package com.ferelin.stockprice.dataInteractor

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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.ferelin.repository.RepositoryManager
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.dataManager.workers.*
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import kotlin.reflect.KProperty

class DataInteractorBuilder(private val mContext: Context) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): DataInteractor {
        return buildDataInteractor(mContext)
    }

    private fun buildDataInteractor(context: Context): DataInteractor {
        val repositoryHelper = RepositoryManager.getInstance(context)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val stylesProvider = StylesProvider(context)
        val errorHandlerWorker = ErrorsWorker(context)
        val firstTimeLaunchWorker = FirstTimeLaunchWorker()
        val dataManager = DataMediator(
            CompaniesWorker(stylesProvider, localInteractorHelper),
            FavouriteCompaniesWorker(
                stylesProvider,
                localInteractorHelper,
                repositoryHelper,
                errorHandlerWorker
            ),
            SearchRequestsWorker(localInteractorHelper),
            firstTimeLaunchWorker
        )
        val networkConnectivityWorker = buildConnectivityWorker(context)

        return DataInteractor(
            repositoryHelper,
            localInteractorHelper,
            dataManager,
            errorHandlerWorker,
            networkConnectivityWorker
        )
    }

    private fun buildConnectivityWorker(context: Context): NetworkConnectivityWorker {
        return NetworkConnectivityWorker(
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
        )
    }
}