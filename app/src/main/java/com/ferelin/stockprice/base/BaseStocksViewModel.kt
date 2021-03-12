package com.ferelin.stockprice.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

abstract class BaseStocksViewModel(
    protected val mCoroutineContext: CoroutineContextProvider,
    protected val mDataInteractor: DataInteractor
) : ViewModel() {

    protected val mRecyclerAdapter = StocksRecyclerAdapter().apply {
        setOnBindCallback { _, company, position ->
            onBindCallback(company, position)
        }
    }
    val recyclerAdapter: StocksRecyclerAdapter
        get() = mRecyclerAdapter

    private val mActionMoveToInfoPagerFragment = MutableSharedFlow<Bundle?>()
    val actionMoveToInfo: SharedFlow<Bundle?>
        get() = mActionMoveToInfoPagerFragment

    open fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.companiesUpdatesShared
                .filter { it is DataNotificator.ItemUpdatedLiveTime || it is DataNotificator.ItemUpdatedQuote }
                .collect { updateRecyclerItem(it) }
        }
    }

    fun onFavouriteIconClicked(company: AdaptiveCompany) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (company.isFavourite) {
                mDataInteractor.removeCompanyFromFavourite(company)
            } else mDataInteractor.addCompanyToFavourite(company)
        }
    }

    fun onStockClicked(company: AdaptiveCompany) {
        /*val arguments = bundleOf(
            InfoPagerFragment.KEY_COMPANY_NAME to company.name,
            InfoPagerFragment.KEY_COMPANY_SYMBOL to company.symbol,
            InfoPagerFragment.KEY_CURRENT_PRICE to company.dayCurrentPrice,
            InfoPagerFragment.KEY_FAVOURITE_ICON_RESOURCE to company.favouriteIconResource,
            InfoPagerFragment.KEY_DAY_DELTA to company.dayProfit
        )
        mActionMoveToInfo.value = arguments*/
    }

    protected fun updateRecyclerItem(notificator: DataNotificator<AdaptiveCompany>) {
        val index = mRecyclerAdapter.companies.indexOf(notificator.data)
        if (index != NULL_INDEX) {
            val previousCompany = mRecyclerAdapter.companies[index]
            //if (notificator.data!! != previousCompany) {
                viewModelScope.launch(mCoroutineContext.Main) {
                    mRecyclerAdapter.updateCompany(notificator.data!!, index)
                }
            //} // TODO
        }
    }

    private fun onBindCallback(company: AdaptiveCompany, position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadCompanyQuote(company.companyProfile.symbol, position).collect()
        }
    }
}