package com.ferelin.stockprice.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseStocksFragment
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.common.StocksItemDecoration
import com.ferelin.stockprice.utils.DataViewModelFactory

class FavouriteFragment : BaseStocksFragment<FavouriteViewModel>() {

    private lateinit var mBinding: FragmentFavouriteBinding

    override val mViewModel: FavouriteViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

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

        mFragmentManager = requireParentFragment().parentFragmentManager

        mBinding.recyclerViewFavourites.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }
}