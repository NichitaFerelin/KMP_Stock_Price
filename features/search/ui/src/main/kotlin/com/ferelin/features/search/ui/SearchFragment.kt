package com.ferelin.features.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.animManager.MotionManager
import com.ferelin.core.ui.view.isLandscapeOrientation
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.core.ui.viewModel.StocksViewModel
import com.ferelin.features.search.ui.databinding.FragmentSearchBinding
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class SearchFragment : BaseFragment<FragmentSearchBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
    get() = FragmentSearchBinding::inflate

  @Inject
  lateinit var viewModelFactory: BaseViewModelFactory<StocksViewModel>
  private val viewModel: SearchViewModel by viewModels(
    factoryProducer = { viewModelFactory }
  )

  private val backPressedCallback by lazy(LazyThreadSafetyMode.NONE) {
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
      .apply { duration = 200L }
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
      .apply { duration = 200L }
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
      .apply { duration = 200L }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) showKeyboard(viewBinding.editTextSearch)
  }

  override fun initUi() {
    with(viewBinding) {
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
      imageViewBack.setOnClick(this@SearchFragment::onBackClick)

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

  private fun onBackClick() {
    if (viewBinding.root.progress == 0F) {
      viewModel.onBack()
    } else {
      viewBinding.root.addTransitionListener(object : MotionManager() {
        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
          viewModel.onBack()
        }
      })
      viewBinding.root.transitionToStart()
    }
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