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
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import com.ferelin.stockprice.ui.stocksSection.common.StockClickListener
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.google.android.material.transition.Hold
import kotlinx.coroutines.launch

abstract class BaseStocksFragment<out T : BaseStocksViewModel>
    : BaseFragment<T>(), StockClickListener {

    protected lateinit var mFragmentManager: FragmentManager
    override fun setUpViewComponents() {
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

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun moveToAboutFragment(
        holder: StocksRecyclerAdapter.StockViewHolder,
        company: AdaptiveCompany
    ) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager.commit {
                setReorderingAllowed(true)
                /*setCustomAnimations(
                    R.anim.scale_in,
                    R.anim.scale_out,
                    R.anim.scale_in,
                    R.anim.scale_out
                )*/
                replace(
                    R.id.fragmentContainer,
                    AboutPagerFragment(
                        company
                    )
                )
                addToBackStack(null)

                addSharedElement(
                    holder.binding.root,
                    "containerAboutPager"
                )
               /* addSharedElement(
                    holder.binding.textViewCompanySymbol,
                    "transitionCompanySymbol"
                )
                addSharedElement(
                    holder.binding.textViewCompanyName,
                    "transitionCompanyName"
                )*/

            }
        }
    }
}