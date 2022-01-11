package com.ferelin.features.stocks.ui.defaults

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
import com.ferelin.features.stocks.databinding.FragmentStocksBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class StocksFragment : BaseFragment<FragmentStocksBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStocksBinding
    get() = FragmentStocksBinding::inflate

  @Inject
  lateinit var viewModelFactory: BaseViewModelFactory<StocksViewModel>
  private val viewModel: StocksViewModel by viewModels(
    factoryProducer = { viewModelFactory }
  )

  override fun onAttach(context: Context) {
    ViewModelProvider(this).get<StocksComponentViewModel>()
      .stocksComponent
      .inject(this)
    super.onAttach(context)
  }

  override fun initUi() {
    viewBinding.recyclerViewStocks.apply {
      adapter = viewModel.stocksAdapter
      itemAnimator = StockItemAnimator()
      addItemDecoration(StockItemDecoration(requireContext()))

      ItemTouchHelper(
        StockSwipeActionCallback(
          onHolderRebound = this@StocksFragment::onHolderRebound,
          onHolderUntouched = this@StocksFragment::onHolderUntouched
        )
      ).attachToRecyclerView(this)
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        companies
          .flowOn(Dispatchers.Main)
          .onEach(this@StocksFragment::onStocks)
          .launchIn(viewLifecycleOwner.lifecycleScope)

        companiesLce
          .flowOn(Dispatchers.Main)
          .onEach { /**/ }
          .launchIn(viewLifecycleOwner.lifecycleScope)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.recyclerViewStocks.adapter = null
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