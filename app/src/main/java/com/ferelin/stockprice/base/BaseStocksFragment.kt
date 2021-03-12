package com.ferelin.stockprice.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.info.InfoPagerFragment
import com.ferelin.stockprice.utils.StockClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseStocksFragment<out T : BaseStocksViewModel>(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : BaseFragment(), StockClickListener {

    protected abstract val mViewModel: T
    protected lateinit var mFragmentManager: FragmentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComponents()
        initObservers()
    }

    protected open fun setUpComponents() {
        mViewModel.recyclerAdapter.setOnStockCLickListener(this)
    }

    protected open fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionMoveToInfo.collect {
                if (it != null) {
                    mFragmentManager.commit {
                        replace(R.id.fragmentContainer, InfoPagerFragment.newInstance(it))
                        addToBackStack(null)
                    }
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