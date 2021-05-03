package com.ferelin.stockprice.ui.stocksSection.stocks

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.databinding.FragmentStocksBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewAnimator
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewController
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.showToast

class StockViewController : BaseStocksViewController<FragmentStocksBinding>() {

    override val mViewAnimator: BaseStocksViewAnimator = BaseStocksViewAnimator()

    override val mStocksRecyclerView: RecyclerView
        get() = viewBinding!!.recyclerViewStocks

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        mStocksRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            mStocksRecyclerView.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(
        stocksAdapter: StocksRecyclerAdapter,
        fragmentManager: FragmentManager
    ) {
        super.fragmentManager = fragmentManager
        mStocksRecyclerView.adapter = stocksAdapter
    }

    fun onError(message: String) {
        showToast(mContext!!, message)
    }
}