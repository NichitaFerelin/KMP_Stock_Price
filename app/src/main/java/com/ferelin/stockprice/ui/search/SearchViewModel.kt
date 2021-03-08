package com.ferelin.stockprice.ui.search

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearch
import com.ferelin.stockprice.ui.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private var mCompanies: List<AdaptiveCompany>? = null
    private lateinit var mStocksRecyclerAdapter: StocksRecyclerAdapter

    private val mPopularRequestsAdapter = SearchesRecyclerAdapter()
    val popularRequestsAdapter: SearchesRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchesRecyclerAdapter()
    val searchesAdapter: SearchesRecyclerAdapter
        get() = mSearchesAdapter

    private var mSectionsVisibility =
        MutableStateFlow<SearchSectionVisibility>(SearchSectionVisibility.SHOW_HINTS_HIDE_RESULTS)
    val sectionsVisibility: StateFlow<SearchSectionVisibility>
        get() = mSectionsVisibility

    fun onSetUpComponents(adapter: StocksRecyclerAdapter) {
        mStocksRecyclerAdapter = adapter
    }

    fun onCompaniesStateResponse(response: DataNotificator<List<AdaptiveCompany>>) {
        if (response is DataNotificator.Success) {
            mCompanies = response.data
        }
    }

    suspend fun onPopularRequestsResponse(response: DataNotificator<List<AdaptiveSearch>>) {
        if (response is DataNotificator.Success) {
            mPopularRequestsAdapter.setData(ArrayList(response.data))
        }
    }

    suspend fun onSearchesRequestsResponse(response: DataNotificator<List<AdaptiveSearch>>) {
        if (response is DataNotificator.Success) {
            mSearchesAdapter.setData(ArrayList(response.data))
        }
    }

    fun onSearchTextChanged(editable: Editable?) {
        editable?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val searchText = it.toString()
                if (searchText.isNotEmpty()) {

                    mSectionsVisibility.value = SearchSectionVisibility.HIDE_HINTS_SHOW_RESULTS
                    val results = mutableListOf<AdaptiveCompany>()
                    mCompanies?.forEach { company ->
                        if (search(company, searchText)) {
                            results.add(company)
                        }
                    }
                    // TODO stable id для анимации
                    mStocksRecyclerAdapter.setCompanies(ArrayList(results))
                } else mSectionsVisibility.value = SearchSectionVisibility.SHOW_HINTS_HIDE_RESULTS
            }
        }
    }
}