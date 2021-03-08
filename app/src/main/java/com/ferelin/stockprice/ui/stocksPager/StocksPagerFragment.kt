package com.ferelin.stockprice.ui.stocksPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentPagerBinding
import com.ferelin.stockprice.ui.search.SearchFragment

class StocksPagerFragment : Fragment() {

    private lateinit var mBinding: FragmentPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComponents()
    }

    private fun setUpComponents() {
        mBinding.viewPager.apply {
            adapter = StocksPagerAdapter(parentFragmentManager, lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // anim
                }
            })
        }

        mBinding.cardViewSearch.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentContainer, SearchFragment()).addToBackStack(null)
            }
        }
    }
}