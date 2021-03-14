package com.ferelin.stockprice.ui.stocksSection.stocksPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentStocksPagerBinding
import com.ferelin.stockprice.ui.stocksSection.search.SearchFragment

class StocksPagerFragment : Fragment() {

    private lateinit var mBinding: FragmentStocksPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComponents()
    }

    private fun setUpComponents() {
        mBinding.viewPager.apply {
            adapter = StocksPagerAdapter(childFragmentManager, lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(position == 0) {
                        TextViewCompat.setTextAppearance(mBinding.textViewHintStocks, R.style.textViewH1)
                        TextViewCompat.setTextAppearance(mBinding.textViewHintFavourite, R.style.textViewH2)
                    } else {
                        TextViewCompat.setTextAppearance(mBinding.textViewHintStocks, R.style.textViewH2)
                        TextViewCompat.setTextAppearance(mBinding.textViewHintFavourite, R.style.textViewH1)
                    }
                }
            })
        }

        mBinding.cardViewSearch.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, SearchFragment())
                addToBackStack(null)
            }
        }
    }
}