package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveLastPrice
import com.ferelin.repository.adaptiveModels.AdaptiveStockCandle
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.shared.SingletonHolder
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Dispatchers
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

    private val mFavouriteCompaniesUpdateState = MutableStateFlow<DataNotificator<AdaptiveCompany>>(
        DataNotificator.Loading()
    )
    val favouriteCompaniesUpdateState: StateFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesUpdateState

    private val mErrorState = MutableStateFlow<DataNotificator<String>>(DataNotificator.Loading())
    val errorState: StateFlow<DataNotificator<String>>
        get() = mErrorState

    override suspend fun prepareData(context: Context) {
        val response = mLocalInteractorHelper.prepareData(context)
        if (response is LocalInteractorResponse.Success) {
            mDataManager.onDataPrepared(response.companies, response.favouriteCompanies)
        } else mErrorState.value = DataNotificator.Error(R.string.errorLoadingData.toString())
    }

    override suspend fun loadStockCandles(
        item: AdaptiveCompany,
        position: Int
    ): Flow<AdaptiveStockCandle> = callbackFlow {
        mRepositoryHelper.loadStockCandles(item, position).collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onStockCandlesLoaded(it)
                offer(it.data)
            } else mErrorState.value = DataNotificator.Error(R.string.errorLoadingData.toString())
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override suspend fun openConnection(): Flow<AdaptiveLastPrice> = callbackFlow {
        mRepositoryHelper.openConnection().collect {
            if (it is RepositoryResponse.Success) {
                mDataManager.onWebSocketResponse(it)
                offer(it.data)
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

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

    companion object : SingletonHolder<DataInteractor, Context>({
        val repositoryHelper = RepositoryManager.getInstance(it)
        val localInteractorHelper = LocalInteractor(repositoryHelper)
        val dataManager = DataManager(localInteractorHelper)
        DataInteractor(repositoryHelper, localInteractorHelper, dataManager)
    })
}