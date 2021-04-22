package com.ferelin.stockprice.dataInteractor

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

class DataInteractorDelegate(private val mContext: Context) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): DataInteractor {
        return buildDataInteractor(mContext)
    }

    private fun buildDataInteractor(context: Context): DataInteractor {
        val repositoryHelper = RepositoryManager.getInstance(context)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val stylesProvider = StylesProvider(context)
        val errorHandlerWorker = ErrorHandlerWorker(context)
        val firstTimeLaunchWorker = FirstTimeLaunchStateWorker()
        val dataManager = DataMediator(
            CompaniesStateWorker(stylesProvider, localInteractorHelper),
            FavouriteCompaniesStateWorker(
                stylesProvider,
                localInteractorHelper,
                repositoryHelper,
                errorHandlerWorker
            ),
            SearchRequestsStateWorker(localInteractorHelper),
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