package com.ferelin.stockprice.ui.stocksSection.base

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStocksViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    protected val mRecyclerAdapter = StocksRecyclerAdapter().apply {
        setOnBindCallback { _, company, position ->
            onBindCallback(company, position)
        }
        setHasStableIds(true)
    }
    val recyclerAdapter: StocksRecyclerAdapter
        get() = mRecyclerAdapter

    private val mActionMoveToInfoPagerFragment = MutableSharedFlow<AdaptiveCompany>()
    val actionMoveToInfo: SharedFlow<AdaptiveCompany>
        get() = mActionMoveToInfoPagerFragment

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.companiesUpdatesShared
                .filter { it is DataNotificator.ItemUpdatedLiveTime || it is DataNotificator.ItemUpdatedQuote }
                .collect {
                    updateNotifyRecyclerItem(it) }
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
        viewModelScope.launch(mCoroutineContext.IO) {
            mActionMoveToInfoPagerFragment.emit(company)
        }
    }

    protected fun setRecyclerItems(items: List<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val newItems = ArrayList(items)
            if (newItems.size > 10) {
                setItemsToRecyclerWithRangeAnim(newItems)
            } else setItemsToRecyclerWithDefaultAnim(newItems)
        }
    }

    protected fun updateNotifyRecyclerItem(notificator: DataNotificator<AdaptiveCompany>) {
        val index = mRecyclerAdapter.companies.indexOf(notificator.data)
        if (index != NULL_INDEX) {
            viewModelScope.launch(mCoroutineContext.Main) {
                mRecyclerAdapter.updateCompany(notificator.data!!, index)
            }
        }
    }

    private fun setItemsToRecyclerWithRangeAnim(newItems: ArrayList<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            for (index in 0 until 10) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addCompanyToEnd(newItems[index])
                }
                delay(30)
            }
            withContext(mCoroutineContext.Main) {
                mRecyclerAdapter.addInRange(newItems, 10, newItems.size - 1)
            }
        }
    }

    private fun setItemsToRecyclerWithDefaultAnim(newItems: ArrayList<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            newItems.forEach {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addCompanyToEnd(it)
                }
                delay(30)
            }
            mRecyclerAdapter.setCompanies(newItems)
        }
    }

    private fun onBindCallback(company: AdaptiveCompany, position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadCompanyQuote(company.companyProfile.symbol, position).collect()
        }
    }
}