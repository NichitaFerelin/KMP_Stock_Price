package com.ferelin.stockprice.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.base.DataViewModelFactory
import com.ferelin.stockprice.utils.StocksClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class StocksBaseFragment : BaseFragment(), StocksClickListener {

    protected abstract val mRecyclerAdapterType: StocksAdapterType
    protected lateinit var mRecyclerAdapter: StocksRecyclerAdapter

    protected val mStocksViewModel: StocksViewModel by activityViewModels {
        DataViewModelFactory(mDataInteractor)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComponents()
        initObservers()
    }

    protected open fun setUpComponents() {
        mRecyclerAdapter = mStocksViewModel.getRecyclerAdapter(mRecyclerAdapterType).also {
            it.setOnBindCallback { _, company, position ->
                lifecycleScope.launch(Dispatchers.IO) {
                    mDataInteractor.loadStockCandles(company, position).collect { response ->
                        mStocksViewModel.onStocksCandlesResponse(response, mRecyclerAdapterType)
                    }
                }
            }
            it.setOnStocksCLickListener(this)
        }
    }

    protected open fun initObservers() {}

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        lifecycleScope.launch(Dispatchers.IO) {
            mStocksViewModel.onFavouriteIconClicked(company)
        }
    }

    override fun onStockClicked(company: AdaptiveCompany) {

    }
}