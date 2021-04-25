package com.ferelin.stockprice.ui.stocksSection.search

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

    private val mActionHideCloseIcon = MutableSharedFlow<Boolean>()
    val actionHideCloseIcon: SharedFlow<Boolean>
        get() = mActionHideCloseIcon

    private var mActionShowHintsHideResults = MutableSharedFlow<Boolean>()
    val actionShowHintsHideResults: SharedFlow<Boolean>
        get() = mActionShowHintsHideResults

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

    private val mActionShowKeyboard = MutableStateFlow(true)
    val actionShowKeyboard: StateFlow<Boolean>
        get() = mActionShowKeyboard

    private val mPopularRequestsAdapter = SearchRecyclerAdapter().apply { setPopularSearches() }
    val popularRequestsAdapter: SearchRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchRecyclerAdapter()
    val searchesAdapter: SearchRecyclerAdapter
        get() = mSearchesAdapter

    private var mTransitionState: Int = 0
    val transitionState: Int
        get() = mTransitionState

    private var mLastTextSearch = ""
    val lastSearchText: String
        get() = mLastTextSearch

    override fun initObserversBlock() {
        super.initObserversBlock()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState
                    .filter { it is DataNotificator.DataPrepared && it.data != null }
                    .take(1)
                    .collect { mCompanies = ArrayList(it.data!!) }
            }
            launch {
                mDataInteractor.searchRequestsState
                    .filter { it is DataNotificator.DataPrepared && it.data != null }
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            onSearchesChanged(it.data!!)
                        }
                    }
            }
            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { it is DataNotificator.ItemUpdatedDefault }
                    .collect { updateRecyclerViewItem(it) }
            }
            launch {
                mDataInteractor.loadSearchRequestsErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
        }
    }

    override fun updateRecyclerViewItem(notificator: DataNotificator<AdaptiveCompany>) {
        val indexInResults = mRecyclerAdapter.companies.indexOf(notificator.data)
        if (indexInResults != NULL_INDEX) {
            viewModelScope.launch(mCoroutineContext.Main) {
                mRecyclerAdapter.updateCompany(notificator.data!!, indexInResults)
            }
        }

        val indexOriginalList = mCompanies?.indexOf(notificator.data) ?: -1
        if (indexOriginalList != NULL_INDEX) {
            mCompanies!![indexOriginalList] = notificator.data!!
        }
    }

    fun onTransition(state: Int) {
        mTransitionState = state
    }

    fun onSearchTextChanged(searchText: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            when {
                mLastTextSearch == searchText -> Unit
                searchText.isEmpty() -> {
                    mLastTextSearch = searchText
                    switchSectionsVisibility(true)
                    mActionHideCloseIcon.emit(true)
                }
                else -> {
                    mActionHideCloseIcon.emit(false)
                    mLastTextSearch = searchText

                    val results = search(searchText)
                    if (results.isNotEmpty()) {
                        val resultArr = ArrayList(results)
                        switchSectionsVisibility(false)
                        if (results.size <= 5) {
                            addToSearched(searchText)
                        }
                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.invalidate()
                            mRecyclerAdapter.setCompanies(resultArr)
                        }
                    } else switchSectionsVisibility(true)
                }
            }
        }
    }

    fun onOpenKeyboard() {
        mActionShowKeyboard.value = false
    }

    private fun search(searchText: String): MutableList<AdaptiveCompany> {
        val itemsToSearchIn = if (searchText.length > mLastTextSearch.length) {
            mRecyclerAdapter.companies
        } else mCompanies

        val results = mutableListOf<AdaptiveCompany>()
        itemsToSearchIn?.forEach { company ->
            if (filterCompanies(company, searchText)) {
                results.add(company)
            }
        }
        return results
    }

    private fun addToSearched(searchText: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.onNewSearch(searchText)
        }
    }

    private fun onSearchesChanged(data: List<AdaptiveSearchRequest>) {
        mSearchesAdapter.setData(ArrayList(data))
    }

    private suspend fun switchSectionsVisibility(showHintsHideResults: Boolean) {
        mActionShowHintsHideResults.emit(showHintsHideResults)
    }
}