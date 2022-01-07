package com.ferelin.features.stocks.ui.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.stocks.StockSwipeActionCallback
import com.ferelin.core.ui.view.stocks.adapter.StockItemAnimator
import com.ferelin.core.ui.view.stocks.adapter.StockItemDecoration
import com.ferelin.core.ui.view.stocks.adapter.StockViewHolder
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.core.ui.viewModel.StocksViewModel
import com.ferelin.features.stocks.ui.databinding.FragmentFavouriteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class FavouriteStocksFragment : BaseFragment<FragmentFavouriteBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavouriteBinding
    get() = FragmentFavouriteBinding::inflate

  @Inject
  lateinit var viewModelFactory: BaseViewModelFactory<StocksViewModel>
  private val viewModel: StocksViewModel by viewModels(
    factoryProducer = { viewModelFactory }
  )

  override fun initUi() {
    viewBinding.recyclerViewFavouriteStocks.apply {
      adapter = viewModel.stocksAdapter
      itemAnimator = StockItemAnimator()
      addItemDecoration(StockItemDecoration(requireContext()))

      ItemTouchHelper(
        StockSwipeActionCallback(
          onHolderRebound = this@FavouriteStocksFragment::onHolderRebound,
          onHolderUntouched = this@FavouriteStocksFragment::onHolderUntouched
        )
      ).attachToRecyclerView(this)
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        companies
          .map { companies -> companies.filter { it.isFavourite } }
          .flowOn(Dispatchers.Main)
          .onEach(this@FavouriteStocksFragment::onStocks)
          .launchIn(viewLifecycleOwner.lifecycleScope)

        companiesLce
          .flowOn(Dispatchers.Main)
          .onEach { /**/ }
          .launchIn(viewLifecycleOwner.lifecycleScope)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.recyclerViewFavouriteStocks.adapter = null
    super.onDestroyView()
  }

  private fun onStocks(viewData: List<StockViewData>) {
    viewModel.stocksAdapter.setData(viewData)
  }

  private fun onHolderRebound(stockViewHolder: StockViewHolder) {

  }

  private fun onHolderUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean) {

  }
}