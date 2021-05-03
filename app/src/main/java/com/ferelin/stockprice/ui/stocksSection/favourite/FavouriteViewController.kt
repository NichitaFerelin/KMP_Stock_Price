package com.ferelin.stockprice.ui.stocksSection.favourite

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewAnimator
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewController
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter

class FavouriteViewController : BaseStocksViewController<FragmentFavouriteBinding>() {

    override val mViewAnimator: BaseStocksViewAnimator = BaseStocksViewAnimator()

    override val mStocksRecyclerView: RecyclerView
        get() = viewBinding!!.recyclerViewFavourites

    override fun onDestroyView() {
        postponeReferencesRemove {
            mStocksRecyclerView.adapter = null
            super.onDestroyView()
        }
    }

    fun onNewItem() {
        mStocksRecyclerView.scrollToPosition(0)
    }

    fun setArgumentsViewDependsOn(
        stocksRecyclerAdapter: StocksRecyclerAdapter,
        fragmentManager: FragmentManager
    ) {
        super.fragmentManager = fragmentManager
        mStocksRecyclerView.adapter = stocksRecyclerAdapter
    }
}