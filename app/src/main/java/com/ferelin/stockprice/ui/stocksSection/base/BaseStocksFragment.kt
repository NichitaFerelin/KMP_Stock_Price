package com.ferelin.stockprice.ui.stocksSection.base

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import com.ferelin.stockprice.ui.stocksSection.common.StockClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseStocksFragment<out T : BaseStocksViewModel>
    : BaseFragment<T>(), StockClickListener {

    protected lateinit var mFragmentManager: FragmentManager

    override fun setUpViewComponents() {
        mViewModel.recyclerAdapter.setOnStockCLickListener(this)
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionMoveToInfo
                .collect {
                    mFragmentManager.commit {
                        replace(R.id.fragmentContainer, AboutPagerFragment(it))
                        addToBackStack(null)
                    }
                }
        }
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onStockClicked(company: AdaptiveCompany) {
        mViewModel.onStockClicked(company)
    }
}