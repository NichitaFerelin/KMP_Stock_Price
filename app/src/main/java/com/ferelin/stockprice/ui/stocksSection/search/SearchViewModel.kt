package com.ferelin.stockprice.ui.stocksSection.search

import android.text.Editable
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import com.ferelin.stockprice.utils.filterCompanies
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    private var mCompanies: ArrayList<AdaptiveCompany>? = null
    private var mLastTextSearch = ""

    private val mPopularRequestsAdapter = SearchRecyclerAdapter().apply { setPopularSearches() }
    val popularRequestsAdapter: SearchRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchRecyclerAdapter()
    val searchesAdapter: SearchRecyclerAdapter
        get() = mSearchesAdapter

    private val mStateActionHideCloseIcon = MutableStateFlow(true)
    val actionHideCloseIcon: StateFlow<Boolean>
        get() = mStateActionHideCloseIcon

    private var mStateActionShowHintsHideResults = MutableStateFlow(true)
    val actionShowHintsHideResults: StateFlow<Boolean>
        get() = mStateActionShowHintsHideResults

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

    override fun initObserversBlock() {
        super.initObserversBlock()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect { mCompanies = ArrayList(it.data!!) }
            }
            launch {
                mDataInteractor.searchRequestsState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            onSearchesChanged(it.data!!)
                        }
                    }
            }
            launch {
                mDataInteractor.searchRequestsUpdateShared
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            onSearchesChanged(it)
                        }
                    }
            }
            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { it is DataNotificator.ItemUpdatedDefault }
                    .collect { updateRecyclerItem(it) }
            }
            launch {
                mDataInteractor.loadSearchRequestsErrorState
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
        }
    }

    override fun updateRecyclerItem(notificator: DataNotificator<AdaptiveCompany>) {
        val indexInResults = mRecyclerAdapter.companies.indexOf(notificator.data)
        val indexOriginalList = mCompanies?.indexOf(notificator.data) ?: -1
        if (indexInResults != NULL_INDEX) {
            viewModelScope.launch(mCoroutineContext.Main) {
                mRecyclerAdapter.updateCompany(notificator.data!!, indexInResults)
            }
        }
        if (indexOriginalList != NULL_INDEX) {
            mCompanies!![indexOriginalList] = notificator.data!!
        }
    }

    fun onSearchTextChanged(editable: Editable?) {
        viewModelScope.launch(mCoroutineContext.IO) {
            editable?.let {
                val searchText = it.toString()
                when {
                    mLastTextSearch == searchText -> {
                        Log.d("Test", "eqal")
                        Unit
                    }
                    searchText.isEmpty() -> {
                        mLastTextSearch = searchText
                        switchSectionsVisibility(true)
                        mStateActionHideCloseIcon.value = true
                    }
                    else -> {
                        mLastTextSearch = searchText
                        Log.d("Test", "Else")
                        val itemsToSearchIn = if (searchText.length > mLastTextSearch.length) {
                            mRecyclerAdapter.companies
                        } else mCompanies

                        val results = mutableListOf<AdaptiveCompany>()
                        itemsToSearchIn?.forEach { company ->
                            if (filterCompanies(company, searchText)) {
                                results.add(company)
                            }
                        }

                        if (results.isNotEmpty()) {
                            val resultArr = ArrayList(results)
                            switchSectionsVisibility(false)
                            if (results.size <= 5) {
                                addToSearched(searchText)
                            }
                            withContext(mCoroutineContext.Main) {
                                mRecyclerAdapter.invalidate()
                                mRecyclerAdapter.setCompaniesWithNotify(resultArr)
                            }
                        } else {
                            switchSectionsVisibility(true)
                            mStateActionHideCloseIcon.value = true
                        }
                    }
                }
                /*if (mLastTextSearch != searchText) {
                    mLastTextSearch = searchText
                    if (searchText.isNotEmpty()) {
                        mStateActionHideCloseIcon.value = false
                        val results = mutableListOf<AdaptiveCompany>()
                        mCompanies?.forEach { company ->
                            if (search(company, searchText)) {
                                results.add(company)
                            }
                        }

                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.invalidate()
                        }

                        if (results.isNotEmpty()) {
                            val resultArray = ArrayList(results)
                            withContext(mCoroutineContext.Main) {
                                mRecyclerAdapter.setCompaniesWithNotify(resultArray)
                            }
                            switchSectionsVisibility(false)
                            if (results.size <= 10) {
                                addToSearched(searchText)
                            }
                        } else switchSectionsVisibility(true)
                    } else {
                        switchSectionsVisibility(true)
                        mStateActionHideCloseIcon.value = true
                    }
                }*/
            }
        }
    }

    private fun addToSearched(searchText: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.onNewSearch(searchText)
        }
    }

    private fun onSearchesChanged(data: List<AdaptiveSearchRequest>) {
        mSearchesAdapter.setData(ArrayList(data))
    }

    private fun switchSectionsVisibility(showHintsHideResults: Boolean) {
        mStateActionShowHintsHideResults.value = showHintsHideResults
    }
}