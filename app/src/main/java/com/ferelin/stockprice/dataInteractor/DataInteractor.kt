package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.StockHistoryConverter
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.NetworkConnectivityWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * [DataInteractor] is MAIN and SINGLE entity for the UI layer interaction with data.
 *   - Providing states of data and errors.
 *   - Sending network requests to Repository using [mRepositoryHelper].
 *   - Sending local requests to Repository using [mLocalInteractorHelper].
 *   - Sending errors to [mErrorsWorker].
 *   - Providing network state using [mNetworkConnectivityWorker].
 *   - Providing states about data loading to [mDataMediator].
 */
class DataInteractor(
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataMediator: DataMediator,
    private val mErrorsWorker: ErrorsWorker,
    private val mNetworkConnectivityWorker: NetworkConnectivityWorker
) : DataInteractorHelper {

    val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.companiesWorker.stateCompanies

    val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.companiesWorker.sharedCompaniesUpdates

    val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.favouriteCompaniesWorker.stateFavouriteCompanies

    val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mDataMediator.favouriteCompaniesWorker.stateCompanyForObserver

    val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.favouriteCompaniesWorker.sharedFavouriteCompaniesUpdates

    val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.stateSearchRequests

    val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.statePopularSearchRequests

    val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorker.stateIsNetworkAvailable

    val stateFirstTimeLaunch: StateFlow<Boolean?>
        get() = mDataMediator.firstTimeLaunchWorker.stateFirstTimeLaunch

    val sharedApiLimitError: SharedFlow<String>
        get() = mErrorsWorker.sharedApiLimitError

    val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mErrorsWorker.sharedPrepareCompaniesError

    val sharedLoadCompanyQuoteError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadCompanyQuoteError

    val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadStockCandlesError

    val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadCompanyNewsError

    val sharedOpenConnectionError: SharedFlow<String>
        get() = mErrorsWorker.sharedOpenConnectionError

    val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadSearchRequestsError

    val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mErrorsWorker.sharedFavouriteCompaniesLimitReached

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
                    is RepositoryResponse.Success -> mDataMediator.onStockCandlesLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadStockCandlesError(
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
                    is RepositoryResponse.Success -> mDataMediator.onCompanyNewsLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadCompanyNewsError(
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

    override suspend fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyQuote(symbol, position, isImportant)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onCompanyQuoteLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadCompanyQuoteError(
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
        return mRepositoryHelper.openWebSocketConnection()
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onWebSocketResponse(it)
                    is RepositoryResponse.Failed -> {
                        if (!stateCompanies.value.data.isNullOrEmpty() && stateIsNetworkAvailable.value) {
                            mErrorsWorker.onOpenConnectionError()
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override suspend fun cacheNewSearchRequest(searchText: String) {
        mDataMediator.cacheNewSearchRequest(searchText)
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

    override fun prepareToWebSocketReconnection() {
        mRepositoryHelper.invalidateWebSocketConnection()
        mDataMediator.subscribeItemsOnLiveTimeUpdates()
    }

    private suspend fun prepareCompaniesData() {
        val responseCompanies = mLocalInteractorHelper.getCompanies()
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataMediator.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorsWorker.onPrepareCompaniesError()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mLocalInteractorHelper.getSearchRequestsHistory()
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataMediator.onSearchRequestsHistoryPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorsWorker.onLoadSearchRequestsError()

    }

    private suspend fun prepareFirstTimeLaunchState() {
        val firstTimeLaunchResponse = mLocalInteractorHelper.getFirstTimeLaunchState()
        mDataMediator.onFirstTimeLaunchStateResponse(firstTimeLaunchResponse)
    }

    private fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(mDataMediator.companiesWorker.companies, symbol)
    }

    companion object : SingletonHolder<DataInteractor, Context>({
        val dataInteractor by DataInteractorBuilder(it)
        dataInteractor
    })
}