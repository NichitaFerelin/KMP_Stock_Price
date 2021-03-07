package com.ferelin.stockprice.ui.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.databinding.FragmentStocksBinding
import com.ferelin.stockprice.ui.common.StocksAdapterType
import com.ferelin.stockprice.ui.common.StocksBaseFragment
import com.ferelin.stockprice.ui.common.StocksItemDecoration
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StocksFragment : StocksBaseFragment() {

    private lateinit var mBinding: FragmentStocksBinding

    override val mRecyclerAdapterType: StocksAdapterType
        get() = StocksAdapterType.Default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpComponents() {
        super.setUpComponents()

        mBinding.recyclerViewStocks.apply {
            adapter = mRecyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(Dispatchers.IO) {

            launch {
                mDataInteractor.companiesState.collect {
                    if (it is DataNotificator.Success) {
                        mRecyclerAdapter.setCompanies(ArrayList(it.data))
                    }
                }
            }

            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    when (it) {
                        is DataNotificator.NewItem -> mRecyclerAdapter.updateCompany(it.data)
                        is DataNotificator.Remove -> mRecyclerAdapter.updateCompany(it.data)
                        else -> Unit
                    }
                }
            }
        }
    }
}