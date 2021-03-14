package com.ferelin.stockprice.ui.aboutSection.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.viewModelFactories.ArgsViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartFragment : BaseFragment<ChartViewModel>() {

    private lateinit var mBinding: FragmentChartBinding

    override val mViewModel: ChartViewModel by viewModels {
        ArgsViewModelFactory(mCoroutineContext, mDataInteractor, arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChartBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionDataChanged.collect {
                withContext(mCoroutineContext.Main) {
                    mBinding.apply {
                        textViewCurrentPrice.text = mViewModel.currentPrice
                        textViewBuyPrice.text = mViewModel.currentPrice
                        textViewDayProfit.text = mViewModel.dayProfit
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?): ChartFragment {
            return ChartFragment().apply {
                arguments = args
            }
        }
    }
}