package com.ferelin.stockprice.ui.stocksSection.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentStocksBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksFragment : BaseStocksFragment<StocksViewModel, StocksViewHelper>() {

    override val mViewHelper: StocksViewHelper = StocksViewHelper()
    override val mViewModel: StocksViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override var mStocksRecyclerView: RecyclerView? = null
    private var mBinding: FragmentStocksBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksBinding.inflate(inflater, container, false).also {
            mStocksRecyclerView = it.recyclerViewStocks
        }
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mFragmentManager = requireParentFragment().parentFragmentManager
        mBinding!!.recyclerViewStocks.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.postponeReferencesRemove {
            mBinding?.recyclerViewStocks?.adapter = null
            mBinding = null
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionShowError.collect {
                withContext(mCoroutineContext.Main) {
                    showToast(it)
                }
            }
        }
    }
}