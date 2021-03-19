package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyDayData
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.StockHistoryConverter
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.dataInteractor.dataManager.DataManager
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.dataManager.workers.CompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.ErrorHandlerWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FavouriteCompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.SearchRequestsStateWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class DataInteractor private constructor(
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataManager: DataManager,
    private val mErrorHandlerWorker: ErrorHandlerWorker
) : DataInteractorHelper {

    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.companiesWorker.companiesState

    val companiesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataManager.companiesWorker.companiesUpdatesShared

    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.favouriteCompaniesWorker.favouriteCompaniesState

    val favouriteCompaniesUpdateShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataManager.favouriteCompaniesWorker.favouriteCompaniesUpdateShared

    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataManager.searchRequestsWorker.searchRequestsState

    val searchRequestsUpdateShared: SharedFlow<List<AdaptiveSearchRequest>>
        get() = mDataManager.searchRequestsWorker.searchRequestsUpdateShared

    val prepareCompaniesErrorState: StateFlow<String>
        get() = mErrorHandlerWorker.prepareCompaniesErrorState

    val loadStockCandlesErrorState: SharedFlow<String>
        get() = mErrorHandlerWorker.loadStockCandlesErrorState

    val loadCompanyNewsErrorState: SharedFlow<String>
        get() = mErrorHandlerWorker.loadCompanyNewsErrorState

    val openConnectionErrorState: StateFlow<String>
        get() = mErrorHandlerWorker.openConnectionErrorState

    val loadSearchRequestsErrorState: SharedFlow<String>
        get() = mErrorHandlerWorker.loadSearchRequestsErrorState

    val favouriteCompaniesLimitReachedState: SharedFlow<String>
        get() = mErrorHandlerWorker.favouriteCompaniesLimitReachedState

    val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override suspend fun prepareData(context: Context) {
        prepareCompaniesData(context)
        prepareSearchesHistory(context)
    }

    override suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadStockCandles(symbol)
            .onEach {
                if (it is RepositoryResponse.Success) {
                    mDataManager.onStockCandlesLoaded(it)
                } else mErrorHandlerWorker.onLoadStockCandlesError()
            }
            .filter { it is RepositoryResponse.Success }
            .map { mDataManager.getCompany(symbol)!! }
    }

    override suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyNews(symbol)
            .onEach {
                if (it is RepositoryResponse.Success) {
                    mDataManager.onCompanyNewsLoaded(it)
                } else mErrorHandlerWorker.onLoadCompanyNewsError()
            }
            .filter { it is RepositoryResponse.Success }
            .map { mDataManager.getCompany(symbol)!! }
    }

    override suspend fun loadCompanyQuote(symbol: String, position: Int): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyQuote(symbol, position)
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { mDataManager.onCompanyQuoteLoaded(it as RepositoryResponse.Success<AdaptiveCompanyDayData>) }
            .map { mDataManager.getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    @FlowPreview
    override suspend fun openConnection(): Flow<AdaptiveCompany> {
        return mRepositoryHelper.openConnection()
            .onEach {
                if (it is RepositoryResponse.Success) {
                    mDataManager.onWebSocketResponse(it)
                } else mErrorHandlerWorker.onOpenConnectionError()
            }
            .filter { it is RepositoryResponse.Success }
            .map { mDataManager.getCompany((it as RepositoryResponse.Success).owner!!)!! }
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
        mDataManager.getCompany(symbol)?.let {
            addCompanyToFavourite(it)
        }
    }

    override suspend fun removeCompanyFromFavourite(symbol: String) {
        mDataManager.getCompany(symbol)?.let {
            removeCompanyFromFavourite(it)
        }
    }

    private suspend fun prepareCompaniesData(context: Context) {
        val responseCompanies = mLocalInteractorHelper.getCompaniesData(context)
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataManager.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorHandlerWorker.onPrepareCompaniesError()
    }

    private suspend fun prepareSearchesHistory(context: Context) {
        val responseSearchesHistory = mLocalInteractorHelper.getSearchesData(context)
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataManager.onSearchesDataPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorHandlerWorker.onLoadSearchRequestsError()
    }

    override fun getCompany(symbol: String): AdaptiveCompany? {
        return mDataManager.getCompany(symbol)
    }

    companion object : SingletonHolder<DataInteractor, Context>({
        val repositoryHelper = RepositoryManager.getInstance(it)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val stylesProvider = StylesProvider(it)
        val errorHandlerWorker = ErrorHandlerWorker(it)
        val dataManager = DataManager(
            CompaniesStateWorker(stylesProvider, localInteractorHelper),
            FavouriteCompaniesStateWorker(
                stylesProvider,
                localInteractorHelper,
                repositoryHelper,
                errorHandlerWorker
            ),
            SearchRequestsStateWorker(localInteractorHelper),
        )
        DataInteractor(repositoryHelper, localInteractorHelper, dataManager, errorHandlerWorker)
    })
}