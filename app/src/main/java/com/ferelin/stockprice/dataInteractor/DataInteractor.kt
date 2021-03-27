package com.ferelin.stockprice.dataInteractor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.StockHistoryConverter
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.dataInteractor.dataManager.DataManager
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.dataManager.workers.*
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class DataInteractor private constructor(
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataManager: DataManager,
    private val mErrorHandlerWorker: ErrorHandlerWorker,
    private val mNetworkConnectivityWorker: NetworkConnectivityWorker
) : DataInteractorHelper {

    val apiLimitError: StateFlow<Boolean>
        get() = mErrorHandlerWorker.apiLimitError

    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.companiesWorker.companiesState

    val companiesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataManager.companiesWorker.companiesUpdatesShared

    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.favouriteCompaniesWorker.favouriteCompaniesState

    val favouriteCompaniesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataManager.favouriteCompaniesWorker.favouriteCompaniesUpdatesShared

    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataManager.searchRequestsWorker.searchRequestsState

    val isNetworkAvailableState: StateFlow<Boolean>
        get() = mNetworkConnectivityWorker.isNetworkAvailableState

    val prepareCompaniesErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.prepareCompaniesErrorShared

    val firstTimeLaunchState: StateFlow<Boolean?>
        get() = mDataManager.firstTimeLaunchStateWorker.firstTimeLaunchState

    val loadCompanyQuoteErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.loadCompanyQuoteErrorShared

    val loadStockCandlesErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.loadStockCandlesErrorShared

    val loadCompanyNewsErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.loadCompanyNewsErrorShared

    val openConnectionErrorState: StateFlow<String>
        get() = mErrorHandlerWorker.openConnectionErrorState

    val loadSearchRequestsErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.loadSearchRequestsErrorShared

    val favouriteCompaniesLimitReachedShared: SharedFlow<String>
        get() = mErrorHandlerWorker.favouriteCompaniesLimitReachedState

    val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override suspend fun prepareData() {
        prepareCompaniesData()
        prepareSearchesHistory()
        prepareFirstTimeLaunchState()
    }

    override suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadStockCandles(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> {
                        mDataManager.onStockCandlesLoaded(it)
                        mErrorHandlerWorker.onSuccessResponse()
                    }
                    is RepositoryResponse.Failed -> {
                        if (isNetworkAvailableState.value) {
                            mErrorHandlerWorker.onLoadStockCandlesErrorGot(
                                it.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { getCompany(symbol)!! }
    }

    override suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyNews(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> {
                        mDataManager.onCompanyNewsLoaded(it)
                        mErrorHandlerWorker.onSuccessResponse()
                    }
                    is RepositoryResponse.Failed -> {
                        if (isNetworkAvailableState.value) {
                            mErrorHandlerWorker.onLoadCompanyNewsErrorGot(
                                it.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { getCompany(symbol)!! }
    }

    override suspend fun loadCompanyQuote(symbol: String, position: Int): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyQuote(symbol, position)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> {
                        mDataManager.onCompanyQuoteLoaded(it)
                        mErrorHandlerWorker.onSuccessResponse()
                    }
                    is RepositoryResponse.Failed -> {
                        if (isNetworkAvailableState.value) {
                            mErrorHandlerWorker.onLoadCompanyQuoteErrorGot(
                                it.message,
                                it.owner ?: ""
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    @FlowPreview
    override suspend fun openConnection(): Flow<AdaptiveCompany> {
        return mRepositoryHelper.openConnection()
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataManager.onWebSocketResponse(it)
                    is RepositoryResponse.Failed -> {
                        if (!companiesState.value.data.isNullOrEmpty() && isNetworkAvailableState.value) {
                            mErrorHandlerWorker.onOpenConnectionErrorGot()
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override suspend fun onNewSearch(searchText: String) {
        mDataManager.onNewSearch(searchText)
    }

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataManager.onAddFavouriteCompany(adaptiveCompany)
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataManager.onRemoveFavouriteCompany(adaptiveCompany)
    }

    override suspend fun addCompanyToFavourite(symbol: String) {
        getCompany(symbol)?.let { addCompanyToFavourite(it) }
    }

    override suspend fun removeCompanyFromFavourite(symbol: String) {
        getCompany(symbol)?.let { removeCompanyFromFavourite(it) }
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalInteractorHelper.setFirstTimeLaunchState(state)
    }

    private suspend fun prepareCompaniesData() {
        val responseCompanies = mLocalInteractorHelper.getCompaniesData()
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataManager.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorHandlerWorker.onPrepareCompaniesErrorGot()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mLocalInteractorHelper.getSearchRequestsHistory()
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataManager.onSearchRequestsHistoryPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorHandlerWorker.onLoadSearchRequestsErrorGot()

    }

    private suspend fun prepareFirstTimeLaunchState() {
        val firstTimeLaunchResponse = mLocalInteractorHelper.getFirstTimeLaunchState()
        mDataManager.onFirstTimeLaunchStateResponse(firstTimeLaunchResponse)
    }

    private fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(mDataManager.companiesWorker.companies, symbol)
    }

    companion object : SingletonHolder<DataInteractor, Context>({
        val repositoryHelper = RepositoryManager.getInstance(it)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val stylesProvider = StylesProvider(it)
        val errorHandlerWorker = ErrorHandlerWorker(it)
        val firstTimeLaunchWorker = FirstTimeLaunchStateWorker()
        val dataManager = DataManager(
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
        val networkConnectivityWorker = NetworkConnectivityWorker(
            it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
        )
        DataInteractor(
            repositoryHelper,
            localInteractorHelper,
            dataManager,
            errorHandlerWorker,
            networkConnectivityWorker
        )
    })
}