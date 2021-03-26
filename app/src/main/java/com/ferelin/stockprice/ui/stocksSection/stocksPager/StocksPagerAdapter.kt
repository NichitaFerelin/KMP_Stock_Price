package com.ferelin.stockprice.ui.stocksSection.stocksPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.stockprice.ui.stocksSection.favourite.FavouriteFragment
import com.ferelin.stockprice.ui.stocksSection.stocks.StocksFragment
import kotlinx.coroutines.FlowPreview

class StocksPagerAdapter(
    fm: FragmentManager,
    lifecycle: LifecycleOwner
) : FragmentStateAdapter(fm, lifecycle.lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    @FlowPreview
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StocksFragment()
            1 -> FavouriteFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}