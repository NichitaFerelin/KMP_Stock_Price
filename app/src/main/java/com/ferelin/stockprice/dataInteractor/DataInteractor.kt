package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse
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

    val searchesRequestsState: StateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>
        get() = mDataManager.searchedRequestsState

    private val mFavouriteCompaniesUpdateState = MutableStateFlow<DataNotificator<AdaptiveCompany>>(
        DataNotificator.Loading()
    )
    val favouriteCompaniesUpdateState: StateFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesUpdateState

    private val mErrorState = MutableStateFlow<DataNotificator<String>>(DataNotificator.Loading())
    val errorState: StateFlow<DataNotificator<String>>
        get() = mErrorState

    override suspend fun prepareData(context: Context) {
        prepareCompaniesData(context)
        prepareSearchesHistory(context)
    }

    override suspend fun loadStockCandles(
        item: AdaptiveCompany,
        position: Int
    ): Flow<AdaptiveStockCandles> = callbackFlow {
        mRepositoryHelper.loadStockCandles(item).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onStockCandlesLoaded(it)
                offer(it.data)
            } else mErrorState.value = DataNotificator.Error(R.string.errorLoadingData.toString())
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompanyNews> = callbackFlow {
        mRepositoryHelper.loadCompanyNews(symbol).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onCompanyNewsLoaded(it)
                offer(it.data)
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override suspend fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<AdaptiveCompanyQuote> = callbackFlow {
        mRepositoryHelper.loadCompanyQuote(symbol, position).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onCompanyQuoteLoaded(it)
                offer(it.data)
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    @FlowPreview
    override suspend fun openConnection(): Flow<AdaptiveLastPrice> = callbackFlow {
        mRepositoryHelper.openConnection().collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onWebSocketResponse(it)
                offer(it.data)
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO).debounce(100)

    override suspend fun subscribeItem(symbol: String) {
        mRepositoryHelper.subscribeItem(symbol)
    }

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mFavouriteCompaniesUpdateState.value = DataNotificator.Loading()
        mDataManager.onAddFavouriteCompany(adaptiveCompany)
        mFavouriteCompaniesUpdateState.value = DataNotificator.NewItem(adaptiveCompany)
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mFavouriteCompaniesUpdateState.value = DataNotificator.Loading()
        mDataManager.onRemoveFavouriteCompany(adaptiveCompany)
        mFavouriteCompaniesUpdateState.value = DataNotificator.Remove(adaptiveCompany)
    }

    private suspend fun prepareCompaniesData(context: Context) {
        val responseCompanies = mLocalInteractorHelper.getCompaniesData(context)
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataManager.onCompaniesDataPrepared(responseCompanies.companies,)
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