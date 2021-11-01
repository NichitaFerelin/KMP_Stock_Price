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
import com.ferelin.core.customView.chart.ChartPastPrices
import com.ferelin.core.params.ChartParams
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_chart.R
import com.ferelin.feature_chart.databinding.FragmentChartBinding
import com.ferelin.feature_chart.viewData.ChartViewMode
import com.ferelin.feature_chart.viewModel.ChartViewModel
import com.ferelin.shared.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChartFragment : BaseFragment<FragmentChartBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChartBinding
        get() = FragmentChartBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ChartViewModel>

    @Inject
    lateinit var stockStyleProvider: StockStyleProvider

    private val viewModel: ChartViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private var selectedCard: CardView? = null
    private var selectedText: TextView? = null

    private var scaleInOut: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        setPriceFields(viewModel.chartParams.stockPrice, viewModel.chartParams.stockProfit)
        restoreSelectedViewMode(viewModel.chartMode)
    }

    override fun initUx() {
        setCardBtnsListeners()
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observePastPrice() }
            launch { observeActualStockPrice() }
        }
    }

    override fun onDestroyView() {
        scaleInOut?.invalidate()
        selectedCard = null
        selectedText = null
        super.onDestroyView()
    }

    private suspend fun observePastPrice() {
        viewModel.pastPriceLoadState.collect { pastPriceLoad ->
            withContext(Dispatchers.Main) {
                when (pastPriceLoad) {
                    is LoadState.Prepared -> {
                        hideProgressBar()
                        onPastPriceChanged(pastPriceLoad.data)
                    }
                    is LoadState.Loading -> showProgressBar()
                    is LoadState.None -> viewModel.loadPastPrices()
                    else -> {
                        hideProgressBar()
                        onError()
                    }
                }
            }
        }
    }

    private suspend fun observeActualStockPrice() {
        viewModel.actualStockPrice.collect { stockPriceViewData ->
            withContext(Dispatchers.Main) {
                setPriceFields(stockPriceViewData.price, stockPriceViewData.profit)
            }
        }
    }

    private fun setCardBtnsListeners() {
        with(viewBinding) {
            cardViewDay.setOnClickListener { onCardClicked(it as CardView) }
            cardViewWeek.setOnClickListener { onCardClicked(it as CardView) }
            cardViewMonth.setOnClickListener { onCardClicked(it as CardView) }
            cardViewHalfYear.setOnClickListener { onCardClicked(it as CardView) }
            cardViewYear.setOnClickListener { onCardClicked(it as CardView) }
            cardViewAll.setOnClickListener { onCardClicked(it as CardView) }
        }
    }

    private fun onCardClicked(card: CardView) {
        if (card == selectedCard) {
            jumpCard(card)
            return
        }

        val selectedChartMode = getCardAttachedViewMode(card)
        viewModel.onNewChartMode(selectedChartMode)

        val attachedTextView = getCardAttachedTextView(card)
        applySelectedStyle(card, attachedTextView)
        applyDefaultStyle(selectedCard!!, selectedText!!)
        saveSelectedViews(card, attachedTextView)
    }

    private fun onError() {
        if (!viewModel.isNetworkAvailable) {
            showSnackbar(getString(R.string.messageNetworkNotAvailable))
        } else {
            showTempSnackbar(getString(R.string.errorUndefined))
        }
    }

    private fun onPastPriceChanged(history: ChartPastPrices) {
        if (history.prices.isNotEmpty()) {
            viewBinding.chartView.setData(history)
            showChart()
            hideProgressBar()
        }
    }

    private fun restoreSelectedViewMode(lastSelectedChartViewMode: ChartViewMode) {
        val (lastCard, lastText) = getAttachedViewsByMode(lastSelectedChartViewMode)
        selectedText = lastText as TextView
        selectedCard = lastCard as CardView

        // Mode 'All' is set by default
        if (lastSelectedChartViewMode == ChartViewMode.All) {
            return
        }

        val (restoredCardView, restoredTextView) = getAttachedViewsByMode(lastSelectedChartViewMode)
        applyDefaultStyle(viewBinding.cardViewAll, viewBinding.textViewAll)
        applySelectedStyle(restoredCardView as CardView, restoredTextView as TextView)
        saveSelectedViews(restoredCardView, restoredTextView)
    }

    private fun saveSelectedViews(newCard: CardView, newText: TextView) {
        selectedCard = newCard
        selectedText = newText
    }

    private fun showProgressBar() {
        if (!viewBinding.progressBar.isVisible) {
            viewBinding.progressBar.isVisible = true
        }
    }

    private fun hideProgressBar() {
        if (viewBinding.progressBar.isVisible) {
            viewBinding.progressBar.isVisible = false
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
        with(viewBinding) {
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
        with(viewBinding) {
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
        with(viewBinding) {
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
        viewBinding.textViewCurrentPrice.text = price
        viewBinding.textViewDayProfit.text = profit

        val profitBackground = stockStyleProvider.getProfitBackground(profit)
        viewBinding.textViewDayProfit.setTextColor(profitBackground)
    }

    private fun showChart() {
        if (!viewBinding.groupChartWidgets.isVisible) {
            TransitionManager.beginDelayedTransition(viewBinding.root)
            viewBinding.groupChartWidgets.isVisible = true
        }
    }

    private fun jumpCard(card: CardView) {
        if (scaleInOut == null) {
            scaleInOut = AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
        }
        scaleInOut!!.setTarget(card)
        scaleInOut!!.start()
    }

    private fun unpackArgs(args: Bundle) {
        args[chartParamsKey]?.let { params ->
            if (params is ChartParams) {
                viewModel.chartParams = params
            }
        }
    }

    companion object {

        private const val chartParamsKey = "c"

        fun newInstance(data: Any?): ChartFragment {
            return ChartFragment().also {
                if (data is ChartParams) {
                    it.arguments = bundleOf(chartParamsKey to data)
                }
            }
        }
    }
}