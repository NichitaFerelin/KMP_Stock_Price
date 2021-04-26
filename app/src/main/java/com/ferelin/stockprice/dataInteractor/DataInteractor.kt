package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.StockHistoryConverter
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.ErrorHandlerWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.NetworkConnectivityWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/*
* The MAIN and SINGLE entity for the UI layer interaction with data.
*   - Providing multi states of data and errors.
*   - Sending requests for data.
*   - Sending notification about data loading.
* */
class DataInteractor(
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataMediator: DataMediator,
    private val mErrorHandlerWorker: ErrorHandlerWorker,
    private val mNetworkConnectivityWorker: NetworkConnectivityWorker
) : DataInteractorHelper {

    val apiLimitError: StateFlow<Boolean>
        get() = mErrorHandlerWorker.apiLimitError

    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataMediator.companiesWorker.companiesState

    val companiesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.companiesWorker.companiesUpdatesShared

    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataMediator.favouriteCompaniesWorker.favouriteCompaniesState

    val favouriteCompaniesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.favouriteCompaniesWorker.favouriteCompaniesUpdatesShared

    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.searchRequestsState

    val isNetworkAvailableState: StateFlow<Boolean>
        get() = mNetworkConnectivityWorker.isNetworkAvailableState

    val prepareCompaniesErrorShared: SharedFlow<String>
        get() = mErrorHandlerWorker.prepareCompaniesErrorShared

    val firstTimeLaunchState: StateFlow<Boolean?>
        get() = mDataMediator.firstTimeLaunchStateWorker.firstTimeLaunchState

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
                        mDataMediator.onStockCandlesLoaded(it)
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
                        mDataMediator.onCompanyNewsLoaded(it)
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
                        mDataMediator.onCompanyQuoteLoaded(it)
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
                    is RepositoryResponse.Success -> mDataMediator.onWebSocketResponse(it)
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
        mDataMediator.onNewSearch(searchText)
    }

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onAddFavouriteCompany(adaptiveCompany)
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onRemoveFavouriteCompany(adaptiveCompany)
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
        val responseCompanies = mLocalInteractorHelper.getCompanies()
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataMediator.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorHandlerWorker.onPrepareCompaniesErrorGot()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mLocalInteractorHelper.getSearchRequestsHistory()
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataMediator.onSearchRequestsHistoryPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorHandlerWorker.onLoadSearchRequestsErrorGot()

    }

    private suspend fun prepareFirstTimeLaunchState() {
        val firstTimeLaunchResponse = mLocalInteractorHelper.getFirstTimeLaunchState()
        mDataMediator.onFirstTimeLaunchStateResponse(firstTimeLaunchResponse)
    }

    private fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(mDataMediator.companiesWorker.companies, symbol)
    }

    companion object : SingletonHolder<DataInteractor, Context>({
        val dataInteractor by DataInteractorDelegate(it)
        dataInteractor
    })
}