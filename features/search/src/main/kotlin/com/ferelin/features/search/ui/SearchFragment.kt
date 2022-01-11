package com.ferelin.features.search.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.animManager.MotionManager
import com.ferelin.core.ui.view.isLandscapeOrientation
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.core.ui.view.stocks.adapter.StockItemDecoration
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.features.search.databinding.FragmentSearchBinding
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class SearchFragment : BaseFragment<FragmentSearchBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
    get() = FragmentSearchBinding::inflate

  @Inject
  lateinit var viewModelFactory: Lazy<BaseViewModelFactory<SearchViewModel>>
  private val viewModel: SearchViewModel by viewModels(
    factoryProducer = { viewModelFactory.get() }
  )

  private val backPressedCallback by lazy(NONE) {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (viewBinding.root.progress == 1F) {
          viewBinding.editTextSearch.setText("")
        } else {
          this.remove()
          requireActivity().onBackPressed()
        }
      }
    }
  }

  override fun onAttach(context: Context) {
    ViewModelProvider(this).get<SearchComponentViewModel>()
      .searchComponent
      .inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) showKeyboard(viewBinding.editTextSearch)
  }

  override fun initUi() {
    with(viewBinding) {
      recyclerViewSearchResults.apply {
        adapter = viewModel.stocksAdapter
        addItemDecoration(StockItemDecoration(requireContext()))
      }
      recyclerViewSearchedHistory.apply {
        adapter = viewModel.searchRequestsAdapter
        addItemDecoration(provideItemDecoration())
      }
      recyclerViewPopularRequests.apply {
        adapter = viewModel.popularSearchRequestsAdapter
        addItemDecoration(provideItemDecoration())
      }
    }
  }

  override fun initUx() {
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      backPressedCallback
    )
    with(viewBinding) {
      imageViewIconClose.setOnClick(this@SearchFragment::onCloseIconClick)
      imageViewBack.setOnClick(viewModel::onBack)

      editTextSearch.addTextChangedListener {
        onSearchTextChanged(it.toString())
      }
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        searchResults
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onSearchResults)
          .launchIn(this)

        searchRequest
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onSearchText)
          .launchIn(this)

        searchRequests
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onSearchRequests)
          .launchIn(this)

        searchRequestsLce
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onSearchRequestsLce)
          .launchIn(this)

        popularSearchRequests
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onPopularSearchRequests)
          .launchIn(this)

        popularSearchRequestsLce
          .flowOn(Dispatchers.Main)
          .onEach(this@SearchFragment::onPopularSearchRequestsLce)
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    hideKeyboard()
    viewBinding.recyclerViewSearchedHistory.adapter = null
    viewBinding.recyclerViewPopularRequests.adapter = null
    super.onDestroyView()
  }

  private fun onSearchResults(results: List<StockViewData>) {
    if (results.isEmpty()) {
      viewBinding.root.transitionToStart()
    } else viewBinding.root.transitionToEnd()

    viewModel.stocksAdapter.setData(results)
  }

  private fun onSearchText(searchText: String) {
    viewBinding.editTextSearch.setText(searchText)
    viewBinding.editTextSearch.setSelection(searchText.length)
  }

  private fun onSearchRequests(searchRequests: List<SearchViewData>) {
    viewModel.searchRequestsAdapter.setData(searchRequests)
  }

  private fun onSearchRequestsLce(lceState: LceState) {
    // notify
  }

  private fun onPopularSearchRequests(searchRequests: List<SearchViewData>) {
    viewModel.popularSearchRequestsAdapter.setData(searchRequests)
  }

  private fun onPopularSearchRequestsLce(lceState: LceState) {
    // notify
  }

  private fun provideItemDecoration(): SearchItemDecoration {
    return requireContext().let {
      if (it.isLandscapeOrientation) {
        SearchItemDecorationLandscape(it)
      } else {
        SearchItemDecoration(it)
      }
    }
  }

  private fun onSearchTextChanged(searchText: String) {
    viewModel.onSearchTextChanged(searchText)
    if (searchText.isEmpty()) hideCloseIcon() else showCloseIcon()
  }

  private fun hideCloseIcon() {
    viewBinding.imageViewIconClose.isVisible = false
  }

  private fun showCloseIcon() {
    viewBinding.imageViewIconClose.isVisible = true
  }

  private fun onCloseIconClick() {
    viewBinding.editTextSearch.setText("")
  }
}