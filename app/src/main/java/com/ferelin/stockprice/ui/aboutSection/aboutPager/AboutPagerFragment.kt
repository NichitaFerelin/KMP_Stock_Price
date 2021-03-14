package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentAboutPagerBinding
import com.ferelin.stockprice.viewModelFactories.ArgsViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutPagerFragment : BaseFragment<AboutPagerViewModel>() {

    private lateinit var mBinding: FragmentAboutPagerBinding

    override val mViewModel: AboutPagerViewModel by viewModels {
        ArgsViewModelFactory(mCoroutineContext, mDataInteractor, arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        mBinding.apply {
            viewPager.adapter = AboutPagerAdapter(childFragmentManager, lifecycle, arguments)
            textViewCompanyName.text = mViewModel.companyName
            textViewCompanySymbol.text = mViewModel.companySymbol
            imageViewStar.setImageResource(mViewModel.companyFavouriteIconResource)
            imageViewStar.setOnClickListener {
                mViewModel.onFavouriteIconClicked()
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.eventIconChanged.collect {
                withContext(mCoroutineContext.Main) {
                    mBinding.imageViewStar.setImageResource(mViewModel.companyFavouriteIconResource)
                }
            }
        }
    }

    companion object {
        const val KEY_CURRENT_PRICE = "current_price"
        const val KEY_DAY_PROFIT = "day_delta"
        const val KEY_COMPANY_SYMBOL = "company_symbol"
        const val KEY_COMPANY_NAME = "company_name"
        const val KEY_FAVOURITE_ICON_RESOURCE = "favourite_icon"
        const val KEY_IS_FAVOURITE = "is_favourite"

        fun newInstance(
            symbol: String,
            name: String,
            favouriteIconResource: Int,
            currentPrice: String,
            dayProfit: String,
            isFavourite: Boolean
        ): AboutPagerFragment {
            return AboutPagerFragment().apply {
                arguments = bundleOf(
                    KEY_COMPANY_SYMBOL to symbol,
                    KEY_COMPANY_NAME to name,
                    KEY_FAVOURITE_ICON_RESOURCE to favouriteIconResource,
                    KEY_CURRENT_PRICE to currentPrice,
                    KEY_DAY_PROFIT to dayProfit,
                    KEY_IS_FAVOURITE to isFavourite
                )
            }
        }
    }
}