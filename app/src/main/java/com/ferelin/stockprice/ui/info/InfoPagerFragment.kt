package com.ferelin.stockprice.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentInfoPagerBinding

class InfoPagerFragment : BaseFragment() {

    private lateinit var mBinding: FragmentInfoPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInfoPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            mBinding.textViewCompanyName.text = it[sCompanyName] as String
            mBinding.textViewCompanySymbol.text = it[sCompanySymbol] as String
            val args = bundleOf(
                InfoPagerAdapter.CURRENT_PRICE_KEY to it[sCurrentPrice],
                InfoPagerAdapter.DAY_DELTA_KEY to it[sDayDelta]
            )
            mBinding.viewPager.adapter = InfoPagerAdapter(parentFragmentManager, lifecycle, args)
        }
    }

    companion object {

        private const val sCurrentPrice = "current_price"
        private const val sDayDelta = "day_delta"
        private const val sCompanySymbol = "company_symbol"
        private const val sCompanyName = "company_name"
        private const val sIsFavourite = "is_favourite"

        fun newInstance(
            currentPrice: String,
            dayDelta: String,
            companySymbol: String,
            companyName: String,
            isFavourite: Boolean
        ): InfoPagerFragment {
            return InfoPagerFragment().apply {
                arguments = bundleOf(
                    sCurrentPrice to "$124.14",
                    sDayDelta to "+1.0",
                    sCompanySymbol to "AAPL",
                    sCompanyName to "AppleInc",
                    sIsFavourite to false
                )
            }
        }
    }
}