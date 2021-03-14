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

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        companies.forEach {
            if (it.isFavourite) {
                mFavouriteCompanies.add(it)
                subscribeCompanyOnLiveTimeUpdates(it)
            }
        }
        mFavouriteCompaniesState.value = DataNotificator.DataPrepared(mFavouriteCompanies)
    }

    suspend fun onCompanyChanged(company: AdaptiveCompany) {
        val companyIndex = mFavouriteCompanies.indexOf(company)
        if (companyIndex != NULL_INDEX) {
            mFavouriteCompanies[companyIndex] = company
            mFavouriteCompaniesState.value = DataNotificator.DataPrepared(mFavouriteCompanies)
            mFavouriteCompaniesUpdateShared.emit(DataNotificator.ItemUpdatedDefault(company))
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany): AdaptiveCompany {
        company.apply {
            isFavourite = true
            companyStyle.favouriteDefaultIconResource = mStylesProvider.getDefaultIconDrawable(true)
            companyStyle.favouriteSingleIconResource = mStylesProvider.getSingleIconDrawable(true)
        }
        mFavouriteCompanies.add(company)

        with(mFavouriteCompaniesState.value) {
            if (this is DataNotificator.DataPrepared) {
                data?.add(company)
            }
        }

        subscribeCompanyOnLiveTimeUpdates(company)
        mFavouriteCompaniesUpdateShared.emit(DataNotificator.NewItemAdded(company))
        mLocalInteractorHelper.updateCompany(company)
        return company
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany): AdaptiveCompany {
        company.apply {
            isFavourite = false
            companyStyle.favouriteDefaultIconResource = mStylesProvider.getDefaultIconDrawable(false)
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