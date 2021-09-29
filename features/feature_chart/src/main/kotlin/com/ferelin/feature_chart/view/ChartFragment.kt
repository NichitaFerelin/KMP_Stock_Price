/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.feature_chart.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.feature_chart.R
import com.ferelin.feature_chart.databinding.FragmentChartBinding
import com.ferelin.feature_chart.utils.SuggestionController
import com.ferelin.feature_chart.utils.points.Marker
import com.ferelin.feature_chart.viewData.ChartPastPrices
import com.ferelin.feature_chart.viewData.ChartViewMode
import com.ferelin.feature_chart.viewData.PastPriceLoadState
import com.ferelin.feature_chart.viewData.StockPriceLoadState
import com.ferelin.feature_chart.viewModel.ChartViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChartFragment : BaseFragment<FragmentChartBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChartBinding
        get() = FragmentChartBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ChartViewModel>

    private val mViewModel: ChartViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val mSuggestionControl = SuggestionController

    private var mSelectedCard: CardView? = null
    private var mSelectedText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        restoreSelectedViewMode(mViewModel.chartMode)
        mViewModel.selectedMarker?.let { restoreChartState(it) }
    }

    override fun initUx() {
        mViewBinding.chartView.setOnTouchListener {
            onChartClicked(
                previousClickedMarker = mViewModel.selectedMarker,
                newClickedMarker = it
            )
        }

        setCardBtnsListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSelectedCard = null
        mSelectedText = null
    }

    private fun setCardBtnsListeners() {
        with(mViewBinding) {
            cardViewDay.setOnClickListener { onCardClicked(it as CardView) }
            cardViewWeek.setOnClickListener { onCardClicked(it as CardView) }
            cardViewMonth.setOnClickListener { onCardClicked(it as CardView) }
            cardViewHalfYear.setOnClickListener { onCardClicked(it as CardView) }
            cardViewYear.setOnClickListener { onCardClicked(it as CardView) }
            cardViewAll.setOnClickListener { onCardClicked(it as CardView) }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch { observePastPrice() }
            launch {
                observeActualStockPrice()
                observeLiveTimePrice()
            }
        }
    }

    private suspend fun observePastPrice() {
        mViewModel.pastPriceLoadState.collect { pastPriceLoad ->
            when (pastPriceLoad) {
                is PastPriceLoadState.Loaded -> {
                    onDataLoadingStateChanged(false)
                    onPastPriceChanged(pastPriceLoad.chartPastPrices)
                }
                is PastPriceLoadState.Loading -> {
                    onDataLoadingStateChanged(true)
                }
                is PastPriceLoadState.None -> {
                    mViewModel.loadPastPrices()
                }
                else -> Unit
            }
        }
    }

    private suspend fun observeActualStockPrice() {
        mViewModel.stockPriceLoad
            .take(1)
            .collect { stockPriceLoad ->
                when (stockPriceLoad) {
                    is StockPriceLoadState.Loaded -> {
                        setPriceFields(
                            price = stockPriceLoad.stockPrice.currentPrice,
                            profit = stockPriceLoad.stockPrice.profit
                        )
                    }
                    is StockPriceLoadState.None -> {
                        mViewModel.loadActualStockPrice()
                    }
                    else -> Unit
                }
            }
    }

    private suspend fun observeLiveTimePrice() {
        mViewModel.liveTimePrice.collect { livePrice ->
            setPriceFields(livePrice.price, livePrice.profit)
        }
    }

    private fun onChartClicked(previousClickedMarker: Marker?, newClickedMarker: Marker) {
        if (previousClickedMarker == newClickedMarker) {
            return
        }

        mViewModel.selectedMarker = newClickedMarker

        hideSuggestion()
        changeSuggestionCoordinates(newClickedMarker)
        updateSuggestionText(newClickedMarker)
        showSuggestion()
    }

    private fun onCardClicked(card: CardView) {
        if (card == mSelectedCard) {
            // mViewAnimator.runScaleInOut(card, null)
            return
        }

        val selectedChartMode = getCardAttachedViewMode(card)
        mViewModel.selectedMarker = null
        mViewModel.onNewChartMode(selectedChartMode)

        hideSuggestion()

        val attachedTextView = getCardAttachedTextView(card)
        applySelectedStyle(card, attachedTextView)
        applyDefaultStyle(mSelectedCard!!, mSelectedText!!)
        saveSelectedViews(card, attachedTextView)
    }

    private fun onPastPriceChanged(history: ChartPastPrices) {
        if (history.prices.isNotEmpty()) {
            mViewBinding.chartView.setData(history)
            showChart()
            hideProgressBar()
        }
    }

    private fun onDataLoadingStateChanged(isDataLoading: Boolean) {
        if (isDataLoading) showProgressBar() else hideProgressBar()
    }

    private fun showChart() {
        if (!mViewBinding.groupChartWidgets.isVisible) {
            TransitionManager.beginDelayedTransition(mViewBinding.root)
            mViewBinding.groupChartWidgets.isVisible = true
        }
    }

    private fun updateSuggestionText(marker: Marker) {
        mViewBinding.includeSuggestion.textViewDate.text = marker.date
        mViewBinding.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun restoreSelectedViewMode(lastSelectedChartViewMode: ChartViewMode) {
        // Mode 'All' is set by default
        if (lastSelectedChartViewMode == ChartViewMode.All) {
            return
        }

        val (restoredCardView, restoredTextView) = getAttachedViewsByMode(lastSelectedChartViewMode)
        applyDefaultStyle(mViewBinding.cardViewAll, mViewBinding.textViewAll)
        applySelectedStyle(restoredCardView as CardView, restoredTextView as TextView)
        saveSelectedViews(restoredCardView, restoredTextView)
    }

    private fun restoreChartState(lastClickedMarker: Marker) {
        mViewBinding.chartView.addOnChartPreparedListener {
            mViewBinding.chartView.restoreMarker(lastClickedMarker)?.let { restoredMarker ->
                onChartClicked(null, restoredMarker)
            }
        }
    }

    private fun changeSuggestionCoordinates(marker: Marker) {
        with(mViewBinding) {
            mSuggestionControl.applyCoordinatesChanges(
                rootSuggestionView = includeSuggestion.root,
                pointView = point,
                plugView = includeSuggestion.viewPlug,
                arrowView = includeSuggestion.viewArrow,
                relativeView = chartView,
                marker = marker
            )
        }
    }

    private fun saveSelectedViews(newCard: CardView, newText: TextView) {
        mSelectedCard = newCard
        mSelectedText = newText
    }

    private fun showSuggestion() {
        // mViewAnimator.runFadeIn(viewBinding.includeSuggestion.root, viewBinding.point)
        mViewBinding.includeSuggestion.root.isVisible = true
    }

    private fun hideSuggestion() {
        // mViewAnimator.runFadeOut(viewBinding.includeSuggestion.root, viewBinding.point)
        mViewBinding.includeSuggestion.root.isVisible = false
    }

    private fun showProgressBar() {
        if (!mViewBinding.progressBar.isVisible) {
            mViewBinding.progressBar.isVisible = true
        }
    }

    private fun hideProgressBar() {
        if (mViewBinding.progressBar.isVisible) {
            mViewBinding.progressBar.isVisible = false
        }
    }

    private fun applySelectedStyle(card: CardView, attachedTextView: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(card.context, R.color.black))
        attachedTextView.setTextColor(ContextCompat.getColor(card.context, R.color.whiteDark))
    }

    private fun applyDefaultStyle(card: CardView, attachedTextView: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(card.context, R.color.whiteDark))
        attachedTextView.setTextColor(ContextCompat.getColor(card.context, R.color.black))
    }

    private fun getAttachedViewsByMode(mode: ChartViewMode): Array<View> {
        with(mViewBinding) {
            return when (mode) {
                ChartViewMode.Year -> arrayOf(cardViewYear, textViewYear)
                ChartViewMode.Months -> arrayOf(cardViewMonth, textViewMonths)
                ChartViewMode.Weeks -> arrayOf(cardViewWeek, textViewWeeks)
                ChartViewMode.Days -> arrayOf(cardViewDay, textViewDays)
                ChartViewMode.SixMonths -> arrayOf(cardViewHalfYear, textViewSixMonths)
                ChartViewMode.All -> arrayOf(cardViewAll, textViewAll)
            }
        }
    }

    private fun getCardAttachedViewMode(card: CardView): ChartViewMode {
        with(mViewBinding) {
            return when (card) {
                cardViewDay -> ChartViewMode.Days
                cardViewWeek -> ChartViewMode.Weeks
                cardViewMonth -> ChartViewMode.Months
                cardViewHalfYear -> ChartViewMode.SixMonths
                cardViewYear -> ChartViewMode.Year
                cardViewAll -> ChartViewMode.All
                else -> throw IllegalStateException("Unexpected card view: $card")
            }
        }
    }

    private fun getCardAttachedTextView(card: CardView): TextView {
        with(mViewBinding) {
            return when (card) {
                cardViewDay -> textViewDays
                cardViewWeek -> textViewWeeks
                cardViewMonth -> textViewMonths
                cardViewHalfYear -> textViewSixMonths
                cardViewYear -> textViewYear
                cardViewAll -> textViewAll
                else -> throw IllegalStateException("Unexpected card view: $card")
            }
        }
    }

    private suspend fun setPriceFields(price: String, profit: String) =
        withContext(mDispatchersProvider.Main) {
            mViewBinding.textViewCurrentPrice.text = price
            mViewBinding.textViewBuyPrice.text = price
            mViewBinding.textViewDayProfit.text = profit
        }

    private fun unpackArgs(args: Bundle) {
        args[ARGS_COMPANY_ID_KEY]?.let { companyId ->
            if (companyId is Int) {
                mViewModel.companyId = companyId
            }
        }
        args[ARGS_COMPANY_TICKER_KEY]?.let { companyTicker ->
            if (companyTicker is String) {
                mViewModel.companyTicker = companyTicker
            }
        }
    }

    companion object {

        const val ARGS_COMPANY_ID_KEY = "id"
        const val ARGS_COMPANY_TICKER_KEY = "ticker"

        fun newInstance(args: Bundle): ChartFragment {
            return ChartFragment().apply {
                arguments = args
            }
        }
    }
}