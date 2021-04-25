package com.ferelin.stockprice.ui.stocksSection.base

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseDataViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStocksViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseDataViewModel(coroutineContextProvider, dataInteractor) {

    protected val mRecyclerAdapter = StocksRecyclerAdapter().apply {
        setOnBindCallback { _, company, position ->
            onBindCallback(company, position)
        }
        setHasStableIds(true)
    }
    val recyclerAdapter: StocksRecyclerAdapter
        get() = mRecyclerAdapter

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.companiesUpdatesShared
                .filter { it is DataNotificator.ItemUpdatedLiveTime || it is DataNotificator.ItemUpdatedQuote }
                .collect { updateRecyclerViewItem(it) }
        }
    }

    fun onFavouriteIconClicked(company: AdaptiveCompany) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (company.isFavourite) {
                mDataInteractor.removeCompanyFromFavourite(company)
            } else mDataInteractor.addCompanyToFavourite(company)
        }
    }

    /*
    * To avoid breaks of shared transition anim
    *  */
    fun postponeReferencesRemove(finally: () -> Unit) {
        viewModelScope.launch(mCoroutineContext.IO) {
            delay(300)
            withContext(mCoroutineContext.Main) {
                finally.invoke()
            }
        }
    }

    protected fun setRecyclerViewItems(items: List<AdaptiveCompany>) {
        mRecyclerAdapter.setCompanies(ArrayList(items))
    }

    protected open fun updateRecyclerViewItem(notificator: DataNotificator<AdaptiveCompany>) {
        val index = mRecyclerAdapter.companies.indexOf(notificator.data)
        if (index != NULL_INDEX) {
            viewModelScope.launch(mCoroutineContext.Main) {
                mRecyclerAdapter.updateCompany(notificator.data!!, index)
            }
        }
    }

    private fun onBindCallback(company: AdaptiveCompany, position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadCompanyQuote(company.companyProfile.symbol, position).collect()
        }
    }
}