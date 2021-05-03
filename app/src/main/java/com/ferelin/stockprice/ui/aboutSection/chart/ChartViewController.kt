package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.transition.TransitionManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.custom.utils.SuggestionControlHelper
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.utils.anim.AnimatorManager
import com.ferelin.stockprice.utils.showToast

class ChartViewController :
    BaseViewController<ChartViewAnimator, FragmentChartBinding>(), ChartControlHelper {

    override val mViewAnimator: ChartViewAnimator = ChartViewAnimator()

    private val mSuggestionControl = SuggestionControlHelper
    private var mSelectedCard: CardView? = null
    private var mSelectedText: TextView? = null

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        saveSelectedViews(viewBinding!!.cardViewAll, viewBinding!!.textViewAll)
    }

    override fun onDestroyView() {
        mSelectedText = null
        mSelectedCard = null
        super.onDestroyView()
    }

    fun setArgumentsViewDependsOn(
        isHistoryForChartEmpty: Boolean,
        lastChartViewMode: ChartViewMode,
        lastClickedMarker: Marker?
    ) {
        if (isHistoryForChartEmpty) viewBinding!!.groupChartWidgets.visibility = View.GONE
        lastClickedMarker?.let { restoreChartState(it) }
        restoreSelectedViewMode(lastChartViewMode)
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

        val cardViewMode = getCardAttachedViewMode(viewBinding!!, card)
        onNewCardClicked.invoke(cardViewMode)

        hideSuggestion()

        val attachedTextView = getCardAttachedTextView(viewBinding!!, card)
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
            target = viewBinding!!.textViewCurrentPrice,
            callback = object : AnimatorManager() {
                override fun onAnimationStart(animation: Animator?) {
                    viewBinding!!.apply {
                        textViewCurrentPrice.text = currentPrice
                        textViewBuyPrice.text = String.format(hintBuyFor, currentPrice)
                        textViewDayProfit.text = dayProfit
                        textViewDayProfit.setTextColor(profitBackgroundResource)
                    }
                }
            })
    }

    fun onStockHistoryChanged(history: AdaptiveCompanyHistoryForChart) {
        if (history.isNotEmpty()) {
            viewBinding!!.chartView.setData(history)
            showChart()
            hideProgressBar()
        }
    }

    fun onError(context: Context, text: String) {
        showToast(context, text)
        hideProgressBar()
    }

    fun onDataLoadingStateChanged(isDataLoading: Boolean) {
        if (isDataLoading) showProgressBar() else hideProgressBar()
    }

    private fun showChart() {
        if (viewBinding!!.groupChartWidgets.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(viewBinding!!.root)
            viewBinding!!.groupChartWidgets.visibility = View.VISIBLE
        }
    }

    private fun updateSuggestionText(marker: Marker) {
        viewBinding!!.includeSuggestion.textViewDate.text = marker.date
        viewBinding!!.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun restoreSelectedViewMode(lastSelectedChartViewMode: ChartViewMode) {
        // Mode 'All' is set by default
        if (lastSelectedChartViewMode == ChartViewMode.All) {
            return
        }

        val (restoredCardView, restoredTextView) = getAttachedViewsByMode(
            viewBinding!!,
            lastSelectedChartViewMode
        )
        applyDefaultStyle(viewBinding!!.cardViewAll, viewBinding!!.textViewAll)
        applySelectedStyle(restoredCardView as CardView, restoredTextView as TextView)
        saveSelectedViews(restoredCardView, restoredTextView)
    }

    private fun restoreChartState(lastClickedMarker: Marker) {
        viewBinding!!.chartView.addOnChartPreparedListener {
            viewBinding!!.chartView.restoreMarker(lastClickedMarker)?.let { restoredMarker ->
                onChartClicked(null, restoredMarker)
            }
        }
    }

    private fun changeSuggestionCoordinates(marker: Marker) {
        with(viewBinding!!) {
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
        mViewAnimator.runFadeIn(viewBinding!!.includeSuggestion.root, viewBinding!!.point)
    }

    private fun hideSuggestion() {
        mViewAnimator.runFadeOut(viewBinding!!.includeSuggestion.root, viewBinding!!.point)
    }

    private fun showProgressBar() {
        if (viewBinding!!.progressBar.visibility != View.VISIBLE) {
            viewBinding!!.progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        if (viewBinding!!.progressBar.visibility != View.GONE) {
            viewBinding!!.progressBar.visibility = View.GONE
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
}