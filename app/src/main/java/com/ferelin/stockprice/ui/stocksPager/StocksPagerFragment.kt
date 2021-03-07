package com.ferelin.stockprice.ui.stocksPager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R

class StocksPagerFragment : Fragment(R.layout.fragment_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        pager.adapter = StocksPagerAdapter(parentFragmentManager, this.lifecycle)
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // anim
            }
        })
    }
}