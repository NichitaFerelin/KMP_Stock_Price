package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.dataManager.DataManager
import com.ferelin.stockprice.dataInteractor.dataManager.DataStylesManager
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class DataInteractor private constructor(
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataManager: DataManager
) : DataInteractorHelper {

    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.companiesState

    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mDataManager.favouriteCompaniesState

    val searchesRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataManager.searchRequestsState

    val searchRequestsUpdateState: SharedFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataManager.searchRequestsUpdateState

    val favouriteCompaniesUpdateState: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataManager.favouriteCompaniesUpdateState

    private val mErrorState = MutableStateFlow<DataNotificator<String>>(DataNotificator.Loading())
    val errorState: StateFlow<DataNotificator<String>>
        get() = mErrorState

    override suspend fun prepareData(context: Context) {
        prepareCompaniesData(context)
        prepareSearchesHistory(context)
    }

    override suspend fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<AdaptiveCompany> =
        callbackFlow {
            mRepositoryHelper.loadStockCandles(symbol, from, to, resolution).collect {
                if (it is RepositoryResponse.Success) {
                    mDataManager.onStockCandlesLoaded(it)?.let { updatedCompany ->
                        offer(updatedCompany)
                    }
                } else mErrorState.value =
                    DataNotificator.Error(R.string.errorLoadingData.toString())
            }
            awaitClose()
        }.flowOn(Dispatchers.IO)

    override suspend fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<AdaptiveCompany> = callbackFlow {
        mRepositoryHelper.loadCompanyNews(symbol, from, to).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onCompanyNewsLoaded(it)?.let { updatedCompany ->
                    offer(updatedCompany)
                }
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override suspend fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<AdaptiveCompany> = callbackFlow {
        mRepositoryHelper.loadCompanyQuote(symbol, position).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onCompanyQuoteLoaded(it)?.let { updatedCompany ->
                    offer(updatedCompany)
                }
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    @FlowPreview
    override suspend fun openConnection(): Flow<AdaptiveCompany> = callbackFlow {
        mRepositoryHelper.openConnection().collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onWebSocketResponse(it)?.let { updatedCompany ->
                    offer(updatedCompany)
                }
            } else mErrorState.value = DataNotificator.Error(R.string.errorWebSocket.toString())
        }
        awaitClose()
    }.flowOn(Dispatchers.IO).debounce(100)

    override suspend fun subscribeItem(symbol: String, openPrice: Double) {
        mRepositoryHelper.subscribeItem(symbol, openPrice)
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

    private suspend fun prepareCompaniesData(context: Context) {
        val responseCompanies = mLocalInteractorHelper.getCompaniesData(context)
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataManager.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorState.value = DataNotificator.Error(R.string.errorLoadingData.toString())
    }

    private suspend fun prepareSearchesHistory(context: Context) {
        val responseSearchesHistory = mLocalInteractorHelper.getSearchesData(context)
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataManager.onSearchesDataPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorState.value = DataNotificator.Error(R.string.errorLoadingData.toString())
    }

    companion object : SingletonHolder<DataInteractor, Context>({
        val repositoryHelper = RepositoryManager.getInstance(it)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val stylesManager = DataStylesManager(it)
        val dataManager = DataManager(localInteractorHelper, stylesManager)
        DataInteractor(repositoryHelper, localInteractorHelper, dataManager)
    })
}