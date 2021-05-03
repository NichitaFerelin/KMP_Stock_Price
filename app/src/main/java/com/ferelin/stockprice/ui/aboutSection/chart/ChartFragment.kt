package com.ferelin.stockprice.ui.aboutSection.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<FragmentChartBinding, ChartViewModel, ChartViewController>() {

    override val mViewController: ChartViewController = ChartViewController()
    override val mViewModel: ChartViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, selectedCompany)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentChartBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpChartWidgetsListeners()
        setUpViewControllerArguments()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateStockHistory() }
            launch { collectStateIsDataLoading() }
            launch { collectStateOnDayDataChanged() }
            launch { collectEventOnError() }
        }
    }

    private suspend fun collectStateIsDataLoading() {
        mViewModel.stateIsDataLoading.collect { isDataLoading ->
            withContext(mCoroutineContext.Main) {
                mViewController.onDataLoadingStateChanged(isDataLoading)
            }
        }
    }

    private suspend fun collectStateOnDayDataChanged() {
        mViewModel.eventOnDayDataChanged.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onDayDataChanged(
                    currentPrice = mViewModel.stockPrice,
                    dayProfit = mViewModel.dayProfit,
                    profitBackgroundResource = mViewModel.profitBackgroundResource,
                    hintBuyFor = resources.getString(R.string.hintBuyFor)
                )
            }
        }
    }

    private suspend fun collectStateStockHistory() {
        mViewModel.stateStockHistory
            .filter { it != null }
            .collect { history ->
                withContext(mCoroutineContext.Main) {
                    mViewController.onStockHistoryChanged(history!!)
                }
            }
    }

    private suspend fun collectEventOnError() {
        mViewModel.eventOnError.collect { message ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(requireContext(), message)
            }
        }
    }

    private fun setUpChartWidgetsListeners() {
        mViewController.viewBinding!!.apply {
            cardViewDay.setOnClickListener { onCardClicked(it) }
            cardViewWeek.setOnClickListener { onCardClicked(it) }
            cardViewMonth.setOnClickListener { onCardClicked(it) }
            cardViewHalfYear.setOnClickListener { onCardClicked(it) }
            cardViewYear.setOnClickListener { onCardClicked(it) }
            cardViewAll.setOnClickListener { onCardClicked(it) }

            chartView.setOnTouchListener { clickedMarker ->
                mViewController.onChartClicked(
                    previousClickedMarker = mViewModel.clickedMarker,
                    newClickedMarker = clickedMarker
                )
                mViewModel.onChartClicked(clickedMarker)
            }
        }
    }

    private fun setUpViewControllerArguments() {
        mViewController.setArgumentsViewDependsOn(
            isHistoryForChartEmpty = mViewModel.isHistoryEmpty,
            lastChartViewMode = mViewModel.chartViewMode,
            lastClickedMarker = mViewModel.clickedMarker
        )
    }

    private fun onCardClicked(card: View) {
        mViewController.onCardClicked(
            card = card as CardView,
            onNewCardClicked = { selectedChartViewMode ->
                mViewModel.onChartControlButtonClicked(selectedChartViewMode)
            }
        )
    }
}