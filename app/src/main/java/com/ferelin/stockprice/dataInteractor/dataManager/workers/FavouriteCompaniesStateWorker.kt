package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.parseDoubleFromStr
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/*
* Worker that is responsible for:
*   - Providing favourite companies(items) for display.
*   - Notification when company was added/removed to favourites.
*   - Subscribing favourite companies for live-time updates
*   - Unsubscribing companies from live-time updates when it was removed from favourite.
*   - Control the limit of favourite companies(look at mCompaniesLimit variable).
*   - Caching data.
* */
class FavouriteCompaniesStateWorker(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mRepositoryHelper: RepositoryManagerHelper,
    private val mErrorHandlerWorker: ErrorHandlerWorker
) {
    private var mFavouriteCompanies: ArrayList<AdaptiveCompany> = arrayListOf()

    private val mFavouriteCompaniesState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveCompany>>>(DataNotificator.Loading())
    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mFavouriteCompaniesState

    private val mFavouriteCompaniesUpdatesShared =
        MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val favouriteCompaniesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesUpdatesShared

    /*
    * Subscribing over 50 items to live-time updates exceeds the limit of
    * web socket => over-limit-companies will not receive updates (or all companies depending
    * the api mood)
    * */
    private val mCompaniesLimit = 50

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        val favouriteCompanies = arrayListOf<AdaptiveCompany>()
        companies.forEach {
            if (it.isFavourite) {
                favouriteCompanies.add(it)
                subscribeCompanyOnLiveTimeUpdates(it)
            }
        }
        favouriteCompanies.sortByDescending { it.favouriteOrderIndex }
        mFavouriteCompanies.addAll(favouriteCompanies)
        mFavouriteCompaniesState.value = DataNotificator.DataPrepared(favouriteCompanies)
    }

    suspend fun onCompanyChanged(company: AdaptiveCompany) {
        mFavouriteCompaniesUpdatesShared.emit(DataNotificator.ItemUpdatedDefault(company))
    }

    suspend fun addCompanyToFavourites(company: AdaptiveCompany): AdaptiveCompany? {
        if (isFavouritesCompaniesLimitExceeded()) {
            mErrorHandlerWorker.onFavouriteCompaniesLimitReached()
            return null
        }

        applyChangesToAddedFavouriteCompany(company)
        subscribeCompanyOnLiveTimeUpdates(company)
        mFavouriteCompanies.add(company)
        mFavouriteCompaniesUpdatesShared.emit(DataNotificator.NewItemAdded(company))
        mLocalInteractorHelper.updateCompany(company)
        return company
    }

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany): AdaptiveCompany {
        applyChangesToRemovedFavouriteCompany(company)
        mRepositoryHelper.unsubscribeItemFromLiveTimeUpdates(company.companyProfile.symbol)
        mFavouriteCompanies.remove(company)
        mFavouriteCompaniesUpdatesShared.emit(DataNotificator.ItemRemoved(company))
        mLocalInteractorHelper.updateCompany(company)
        return company
    }

    private fun isFavouritesCompaniesLimitExceeded(): Boolean {
        return mFavouriteCompanies.size >= mCompaniesLimit
    }

    private fun subscribeCompanyOnLiveTimeUpdates(company: AdaptiveCompany) {
        mRepositoryHelper.subscribeItemToLiveTimeUpdates(
            company.companyProfile.symbol,
            parseDoubleFromStr(company.companyDayData.openPrice)
        )
    }

    private fun applyChangesToRemovedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = false
            companyStyle.favouriteDefaultIconResource =
                mStylesProvider.getDefaultIconDrawable(false)
            companyStyle.favouriteSingleIconResource = mStylesProvider.getSingleIconDrawable(false)
        }
    }

    private fun applyChangesToAddedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            companyStyle.favouriteDefaultIconResource =
                mStylesProvider.getDefaultIconDrawable(true)
            companyStyle.favouriteSingleIconResource =
                mStylesProvider.getSingleIconDrawable(true)

            val orderIndex = mFavouriteCompanies.lastOrNull()?.favouriteOrderIndex?.plus(1) ?: 0
            favouriteOrderIndex = orderIndex
        }
    }
}