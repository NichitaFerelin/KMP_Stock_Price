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
            mBinding.textViewCompanyName.text = it[KEY_COMPANY_NAME] as String
            mBinding.textViewCompanySymbol.text = it[KEY_COMPANY_SYMBOL] as String
            //mBinding.imageViewStar.background = ContextCompat.getDrawable(requireContext(), it[KEY_FAVOURITE_ICON_RESOURCE] as Int)
            val args = bundleOf(
                InfoPagerAdapter.CURRENT_PRICE_KEY to it[KEY_CURRENT_PRICE],
                InfoPagerAdapter.DAY_DELTA_KEY to it[KEY_DAY_DELTA]
            )
            mBinding.viewPager.adapter = InfoPagerAdapter(parentFragmentManager, lifecycle, args)
        }
    }

    companion object {

        const val KEY_CURRENT_PRICE = "current_price"
        const val KEY_DAY_DELTA = "day_delta"
        const val KEY_COMPANY_SYMBOL = "company_symbol"
        const val KEY_COMPANY_NAME = "company_name"
        const val KEY_FAVOURITE_ICON_RESOURCE = "favourite_icon"

        fun newInstance(bundle: Bundle): InfoPagerFragment {
            return InfoPagerFragment().apply {
                arguments = bundle
            }
        }
    }
}