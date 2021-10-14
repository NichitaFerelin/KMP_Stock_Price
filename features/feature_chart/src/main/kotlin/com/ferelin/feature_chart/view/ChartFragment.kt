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

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.ferelin.core.params.ChartParams
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.view.chart.ChartPastPrices
import com.ferelin.core.view.chart.SuggestionController
import com.ferelin.core.view.chart.points.Marker
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_chart.R
import com.ferelin.feature_chart.databinding.FragmentChartBinding
import com.ferelin.feature_chart.viewData.ChartViewMode
import com.ferelin.feature_chart.viewModel.ChartViewModel
import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChartFragment : BaseFragment<FragmentChartBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChartBinding
        get() = FragmentChartBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ChartViewModel>

    @Inject
    lateinit var mStockStyleProvider: StockStyleProvider

    private val mViewModel: ChartViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val mViewsPropertyAnimator = mutableSetOf<View>()

    private val mSuggestionControl = SuggestionController

    private var mSelectedCard: CardView? = null
    private var mSelectedText: TextView? = null

    private var mScaleInOut: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        restoreSelectedViewMode(mViewModel.chartMode)
        restoreChartState(mViewModel.selectedMarker)
        setPriceFields(mViewModel.chartParams.stockPrice, mViewModel.chartParams.stockProfit)
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

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch { observePastPrice() }
            launch { observeActualStockPrice() }
        }
    }

    override fun onDestroyView() {
        mViewsPropertyAnimator.forEach { it.animate().cancel() }
        mScaleInOut?.invalidate()
        mSelectedCard = null
        mSelectedText = null
        super.onDestroyView()
    }

    private suspend fun observePastPrice() {
        mViewModel.pastPriceLoadState.collect { pastPriceLoad ->
            when (pastPriceLoad) {
                is LoadState.Prepared -> {
                    withContext(mDispatchersProvider.Main) {
                        onDataLoadingStateChanged(false)
                        onPastPriceChanged(pastPriceLoad.data)
                    }
                }
                is LoadState.Loading -> onDataLoadingStateChanged(true)
                is LoadState.None -> mViewModel.loadPastPrices()
                else -> Unit
            }
        }
    }

    private suspend fun observeActualStockPrice() {
        mViewModel.actualStockPrice.collect { stockPrice ->
            withContext(mDispatchersProvider.Main) {
                setPriceFields(stockPrice.currentPrice, stockPrice.profit)
            }
        }
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
            jumpCard(card)
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
        if (isDataLoading) {
            showProgressBar()
        } else {
            hideProgressBar()
        }
    }

    private fun updateSuggestionText(marker: Marker) {
        mViewBinding.includeSuggestion.textViewDate.text = marker.date
        mViewBinding.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun restoreSelectedViewMode(lastSelectedChartViewMode: ChartViewMode) {
        val (lastCard, lastText) = getAttachedViewsByMode(lastSelectedChartViewMode)
        mSelectedText = lastText as TextView
        mSelectedCard = lastCard as CardView

        // Mode 'All' is set by default
        if (lastSelectedChartViewMode == ChartViewMode.All) {
            return
        }

        val (restoredCardView, restoredTextView) = getAttachedViewsByMode(lastSelectedChartViewMode)
        applyDefaultStyle(mViewBinding.cardViewAll, mViewBinding.textViewAll)
        applySelectedStyle(restoredCardView as CardView, restoredTextView as TextView)
        saveSelectedViews(restoredCardView, restoredTextView)
    }

    private fun restoreChartState(lastClickedMarker: Marker?) {
        mViewModel.pastPriceLoadState.value.let { pastPriceState ->
            if (pastPriceState is LoadState.Prepared) {
                onPastPriceChanged(pastPriceState.data)
            } else {
                mViewBinding.groupChartWidgets.isVisible = false
            }
        }

        lastClickedMarker?.let { marker ->
            mViewBinding.chartView.addOnChartPreparedListener {
                val restoredMarker = mViewBinding.chartView.restoreMarker(marker)
                if (restoredMarker != null) {
                    onChartClicked(null, restoredMarker)
                }
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
        runFadeIn(mViewBinding.includeSuggestion.root, mViewBinding.point)
    }

    private fun hideSuggestion() {
        runFadeOut(mViewBinding.includeSuggestion.root, mViewBinding.point)
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

    private fun setPriceFields(price: String, profit: String) {
        mViewBinding.textViewCurrentPrice.text = price

        val profitBackground = mStockStyleProvider.getProfitBackground(profit)
        mViewBinding.textViewDayProfit.text = profit
        mViewBinding.textViewDayProfit.setTextColor(profitBackground)

        mViewBinding.textViewBuyPrice.text =
            String.format(getString(R.string.hintBuyFor), price)
    }

    private fun showChart() {
        if (!mViewBinding.groupChartWidgets.isVisible) {
            TransitionManager.beginDelayedTransition(mViewBinding.root)
            mViewBinding.groupChartWidgets.isVisible = true
        }
    }

    private fun jumpCard(card: CardView) {
        if (mScaleInOut == null) {
            mScaleInOut = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
        }
        mScaleInOut!!.setTarget(card)
        mScaleInOut!!.start()
    }

    private fun runFadeIn(vararg targets: View) {
        targets.forEach { view ->
            mViewsPropertyAnimator.add(view)
            view.animate().alpha(1F).duration = 150L
        }
    }

    private fun runFadeOut(vararg targets: View) {
        targets.forEach { view ->
            mViewsPropertyAnimator.add(view)
            view.animate().alpha(0F).duration = 150L
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[sChartParamsKey]?.let { params ->
            if (params is ChartParams) {
                mViewModel.chartParams = params
            }
        }
    }

    companion object {

        private const val sChartParamsKey = "c"

        fun newInstance(data: Any?): ChartFragment {
            return ChartFragment().also {
                if (data is ChartParams) {
                    it.arguments = bundleOf(sChartParamsKey to data)
                }
            }
        }
    }
}