package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import com.ferelin.stockprice.utils.parseDoubleFromStr
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class FavouriteCompaniesStateWorker(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mRepositoryHelper: RepositoryManagerHelper,
) {
    private var mFavouriteCompanies: ArrayList<AdaptiveCompany> = arrayListOf()

    private val mFavouriteCompaniesState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveCompany>>>(DataNotificator.Loading())
    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mFavouriteCompaniesState

    private val mFavouriteCompaniesUpdateShared =
        MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val favouriteCompaniesUpdateShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesUpdateShared


    /*
    * Subscribing over 50 items to live-time updates exceeds the limit of
    * web socket => over-limit-companies will not receive updates (or all companies depending
    * the api mood)
    * */
    private val mCompaniesLimit = 50
    private val mErrorFavouriteCompaniesLimitReached = MutableSharedFlow<Unit>()
    val errorFavouriteCompaniesLimitReached: SharedFlow<Unit>
        get() = mErrorFavouriteCompaniesLimitReached


    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        val favouriteCompanies = arrayListOf<AdaptiveCompany>()
        companies.forEach {
            if (it.isFavourite/* && !mFavouriteCompanies.contains(it)*/) {
                favouriteCompanies.add(it)
                subscribeCompanyOnLiveTimeUpdates(it)
            }
        }
        favouriteCompanies.sortByDescending { it.favouriteOrderIndex }
        mFavouriteCompanies.addAll(favouriteCompanies)
        mFavouriteCompaniesState.value = DataNotificator.DataPrepared(favouriteCompanies)
    }

    suspend fun onCompanyChanged(company: AdaptiveCompany) {
        val companyIndex = mFavouriteCompanies.indexOf(company)
        if (companyIndex != NULL_INDEX) {
            mFavouriteCompanies[companyIndex] = company
            mFavouriteCompaniesState.value = DataNotificator.DataPrepared(mFavouriteCompanies)
            mFavouriteCompaniesUpdateShared.emit(DataNotificator.ItemUpdatedDefault(company))
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany): AdaptiveCompany? {
        return when {
            mFavouriteCompanies.size >= mCompaniesLimit -> {
                mErrorFavouriteCompaniesLimitReached.emit(Unit)
                null
            }
            mFavouriteCompanies.contains(company) -> null
            else -> {
                company.apply {
                    isFavourite = true
                    companyStyle.favouriteDefaultIconResource =
                        mStylesProvider.getDefaultIconDrawable(true)
                    companyStyle.favouriteSingleIconResource =
                        mStylesProvider.getSingleIconDrawable(true)

                    val orderIndex = mFavouriteCompanies
                        .lastOrNull()
                        ?.favouriteOrderIndex?.plus(1) ?: 0
                    favouriteOrderIndex = orderIndex
                }
                mFavouriteCompanies.add(company)

                subscribeCompanyOnLiveTimeUpdates(company)
                mFavouriteCompaniesUpdateShared.emit(DataNotificator.NewItemAdded(company))
                mLocalInteractorHelper.updateCompany(company)
                company
            }
        }
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany): AdaptiveCompany {
        company.apply {
            isFavourite = false
            companyStyle.favouriteDefaultIconResource =
                mStylesProvider.getDefaultIconDrawable(false)
            companyStyle.favouriteSingleIconResource = mStylesProvider.getSingleIconDrawable(false)
        }
        mFavouriteCompanies.remove(company)

        with(mFavouriteCompaniesState.value) {
            if (this is DataNotificator.DataPrepared) {
                data?.remove(company)
            }
        }

        mRepositoryHelper.unsubscribeItemFromLiveTimeUpdates(company.companyProfile.symbol)
        mFavouriteCompaniesUpdateShared.emit(DataNotificator.ItemRemoved(company))
        mLocalInteractorHelper.updateCompany(company)
        return company
    }

    private fun subscribeCompanyOnLiveTimeUpdates(company: AdaptiveCompany) {
        mRepositoryHelper.subscribeItemToLiveTimeUpdates(
            company.companyProfile.symbol,
            parseDoubleFromStr(company.companyDayData.openPrice)
        )
    }
}