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

package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.ChartStockHistory
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.custom.utils.SuggestionControlHelper
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.utils.anim.AnimatorManager

class ChartViewController : BaseViewController<ChartViewAnimator, FragmentChartBinding>() {

    override val mViewAnimator: ChartViewAnimator = ChartViewAnimator()

    private val mSuggestionControl = SuggestionControlHelper
    private var mSelectedCard: CardView? = null
    private var mSelectedText: TextView? = null

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        saveSelectedViews(viewBinding.cardViewAll, viewBinding.textViewAll)
    }

    override fun onDestroyView() {
        mSelectedText = null
        mSelectedCard = null
        super.onDestroyView()
    }

    fun setArgumentsViewDependsOn(
        isHistoryForChartEmpty: Boolean,
        lastChartViewMode: ChartViewMode,
        lastClickedMarker: Marker?,
        selectedCompany: AdaptiveCompany
    ) {
        if (isHistoryForChartEmpty) viewBinding.groupChartWidgets.visibility = View.GONE
        lastClickedMarker?.let { restoreChartState(it) }
        restoreSelectedViewMode(lastChartViewMode)
        onDayDataChanged(
            currentPrice = selectedCompany.companyDayData.currentPrice,
            dayProfit = selectedCompany.companyDayData.profit,
            profitBackgroundResource = selectedCompany.companyStyle.dayProfitBackground,
            hintBuyFor = context.resources.getString(R.string.hintBuyFor)
        )
    }

    fun onChartClicked(previousClickedMarker: Marker?, newClickedMarker: Marker) {
        if (previousClickedMarker == newClickedMarker) {
            return
        }

        hideSuggestion()
        changeSuggestionCoordinates(newClickedMarker)
        updateSuggestionText(newClickedMarker)
        showSuggestion()
    }

    fun onCardClicked(
        card: CardView,
        onNewCardClicked: (ChartViewMode) -> Unit
    ) {
        if (card == mSelectedCard) {
            mViewAnimator.runScaleInOut(card, null)
            return
        }

        val cardViewMode = getCardAttachedViewMode(card)
        onNewCardClicked.invoke(cardViewMode)

        hideSuggestion()

        val attachedTextView = getCardAttachedTextView(card)
        applySelectedStyle(card, attachedTextView)
        applyDefaultStyle(mSelectedCard!!, mSelectedText!!)
        saveSelectedViews(card, attachedTextView)

        mViewAnimator.runScaleInOut(card)
    }

    fun onDayDataChanged(
        currentPrice: String,
        dayProfit: String,
        profitBackgroundResource: Int,
        hintBuyFor: String
    ) {
        mViewAnimator.runScaleInOut(
            target = viewBinding.textViewCurrentPrice,
            callback = object : AnimatorManager() {
                override fun onAnimationStart(animation: Animator?) {
                    viewBinding.run {
                        textViewCurrentPrice.text = currentPrice
                        textViewBuyPrice.text = String.format(hintBuyFor, currentPrice)
                        textViewDayProfit.text = dayProfit
                        textViewDayProfit.setTextColor(profitBackgroundResource)
                    }
                }
            })
    }

    fun onStockHistoryChanged(history: ChartStockHistory) {
        if (history.isNotEmpty()) {
            viewBinding.chartView.setData(history)
            showChart()
            hideTextViewError()
            hideProgressBar()
        }
    }

    fun onError(text: String) {
        viewBinding.textViewError.text = text
        showTextViewError()
        hideProgressBar()
    }

    fun onDataLoadingStateChanged(isDataLoading: Boolean) {
        if (isDataLoading) showProgressBar() else hideProgressBar()
    }

    private fun showTextViewError() {
        if (!viewBinding.textViewError.isVisible) {
            viewBinding.textViewError.isVisible = true
        }
    }

    private fun hideTextViewError() {
        if (viewBinding.textViewError.isVisible) {
            viewBinding.textViewError.isVisible = false
        }
    }

    private fun showChart() {
        if (!viewBinding.groupChartWidgets.isVisible) {
            TransitionManager.beginDelayedTransition(viewBinding.root)
            viewBinding.groupChartWidgets.isVisible = true
        }
    }

    private fun updateSuggestionText(marker: Marker) {
        viewBinding.includeSuggestion.textViewDate.text = marker.date
        viewBinding.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun restoreSelectedViewMode(lastSelectedChartViewMode: ChartViewMode) {
        // Mode 'All' is set by default
        if (lastSelectedChartViewMode == ChartViewMode.All) {
            return
        }

        val (restoredCardView, restoredTextView) = getAttachedViewsByMode(lastSelectedChartViewMode)
        applyDefaultStyle(viewBinding.cardViewAll, viewBinding.textViewAll)
        applySelectedStyle(restoredCardView as CardView, restoredTextView as TextView)
        saveSelectedViews(restoredCardView, restoredTextView)
    }

    private fun restoreChartState(lastClickedMarker: Marker) {
        viewBinding.chartView.addOnChartPreparedListener {
            viewBinding.chartView.restoreMarker(lastClickedMarker)?.let { restoredMarker ->
                onChartClicked(null, restoredMarker)
            }
        }
    }

    private fun changeSuggestionCoordinates(marker: Marker) {
        with(viewBinding) {
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
        mViewAnimator.runFadeIn(viewBinding.includeSuggestion.root, viewBinding.point)
    }

    private fun hideSuggestion() {
        mViewAnimator.runFadeOut(viewBinding.includeSuggestion.root, viewBinding.point)
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
}