package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.base.BaseViewHelper
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import com.ferelin.stockprice.ui.stocksSection.common.StockClickListener
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.google.android.material.transition.Hold
import kotlinx.coroutines.launch

abstract class BaseStocksFragment<out T : BaseStocksViewModel, out V : BaseViewHelper>
    : BaseFragment<T, V>(), StockClickListener {

    protected var mFragmentManager: FragmentManager? = null

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewModel.recyclerAdapter.setOnStockCLickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onStockClicked(
        stockViewHolder: StocksRecyclerAdapter.StockViewHolder,
        company: AdaptiveCompany
    ) {
        moveToAboutFragment(stockViewHolder, company)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeTransition(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFragmentManager = null
        mViewModel.recyclerAdapter.removeListeners()
    }

    private fun moveToAboutFragment(
        holder: StocksRecyclerAdapter.StockViewHolder,
        company: AdaptiveCompany
    ) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager?.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, AboutPagerFragment(company))
                addToBackStack(null)
                addSharedElement(
                    holder.binding.root,
                    resources.getString(R.string.transitionAboutPager)
                )
            }
        }
    }

    private fun postponeTransition(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}