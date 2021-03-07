package com.ferelin.stockprice.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.common.StocksAdapterType
import com.ferelin.stockprice.ui.common.StocksBaseFragment
import com.ferelin.stockprice.ui.common.StocksItemDecoration
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteFragment : StocksBaseFragment() {

    private lateinit var mBinding: FragmentFavouriteBinding

    override val mRecyclerAdapterType: StocksAdapterType
        get() = StocksAdapterType.Favourite

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpComponents() {
        super.setUpComponents()

        mBinding.recyclerViewFavourites.apply {
            adapter = mRecyclerAdapter.also { it.setOnStocksCLickListener(this@FavouriteFragment) }
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(Dispatchers.IO) {

            launch {
                mDataInteractor.favouriteCompaniesState.collect {
                    if (it is DataNotificator.Success) {
                        mRecyclerAdapter.setCompanies(ArrayList(it.data))
                        it.data.forEach { mDataInteractor.subscribeItem(it.symbol) }
                    }
                }
            }

            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    when (it) {
                        is DataNotificator.NewItem -> {
                            mRecyclerAdapter.addCompany(it.data)
                            mDataInteractor.subscribeItem(it.data.symbol)
                        }
                        // TODO try to unsubscribe
                        is DataNotificator.Remove -> mRecyclerAdapter.removeCompany(it.data)
                        else -> Unit
                    }
                }
            }

            launch {
                mDataInteractor.openConnection().collect {
                    it.company?.let { mRecyclerAdapter.updateCompany(it) }
                }
            }
        }
    }
}