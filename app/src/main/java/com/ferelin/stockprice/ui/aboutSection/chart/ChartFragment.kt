package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.custom.utils.SuggestionControlHelper
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.utils.AnimatorManager
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<ChartViewModel, ChartViewHelper>() {

    override val mViewHelper: ChartViewHelper = ChartViewHelper()
    override val mViewModel: ChartViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, selectedCompany)
    }

    private var mBinding: FragmentChartBinding? = null

    private var mCurrentActiveCard: CardView? = null
    private var mCurrentActiveText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChartBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            setSelectedViews(mBinding!!.cardViewAll, mBinding!!.textViewAll)
            setUpChartWidgetsListeners()
            withContext(mCoroutineContext.Main) {
                restoreSelectedType()
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.hasDataForChartState.collect { hasData ->
                    withContext(mCoroutineContext.Main) {
                        switchChartWidgetsVisibility(hasData)
                    }
                }
            }
            launch {
                mViewModel.eventDataChanged.collect {
                    withContext(mCoroutineContext.Main) {
                        onDayDataChanged()
                    }
                }
            }
            launch {
                mViewModel.eventStockHistoryChanged.collect {
                    withContext(mCoroutineContext.Main) {
                        mBinding!!.chartView.setData(it)
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showToast(it)
                        mBinding!!.progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mCurrentActiveText = null
        mCurrentActiveCard = null
    }

    private fun onChartClicked(marker: Marker) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            prepareToChartDataChanges()
            withContext(mCoroutineContext.IO) {
                mBinding!!.apply {
                    SuggestionControlHelper.applyCoordinatesChanges(
                        requireContext(),
                        includeSuggestion.root,
                        point,
                        includeSuggestion.viewPlug,
                        includeSuggestion.viewArrow,
                        chartView,
                        marker
                    )
                }
                withContext(mCoroutineContext.Main) {
                    updateSuggestionText(marker)
                    showSuggestion()
                }
            }
        }
    }

    private fun onCardClicked(
        card: CardView,
        attachedTextView: TextView,
        selectedType: ChartSelectedType
    ) {
        val animationCallback: Animator.AnimatorListener? = if (card != mCurrentActiveCard) {
            hideSuggestion()
            mViewModel.onChartControlButtonClicked(selectedType)
            object : AnimatorManager() {
                override fun onAnimationStart(animation: Animator?) {
                    switchCardsViewStyle(card, attachedTextView)
                    setSelectedViews(card, attachedTextView)
                }
            }
        } else null
        mViewHelper.runScaleInOut(card, animationCallback)
    }

    private fun onDayDataChanged() {
        mViewHelper.runScaleInOut(mBinding!!.textViewCurrentPrice, object : AnimatorManager() {
            override fun onAnimationStart(animation: Animator?) {
                mBinding!!.apply {
                    textViewCurrentPrice.text = mViewModel.currentPrice
                    textViewBuyPrice.text = String.format(
                        resources.getString(R.string.hintBuyFor),
                        mViewModel.currentPrice
                    )
                    textViewDayProfit.text = mViewModel.dayProfit
                    textViewDayProfit.setTextColor(mViewModel.profitBackground)
                }
            }
        })
    }

    private fun updateSuggestionText(marker: Marker) {
        mBinding!!.includeSuggestion.textViewDate.text = marker.date
        mBinding!!.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun switchCardsViewStyle(card: CardView, attachedTextView: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        attachedTextView.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.whiteDark)
        )
        mCurrentActiveCard!!.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.whiteDark)
        )
        mCurrentActiveText!!.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.black)
        )
    }

    private fun restoreSelectedType() {
        mBinding!!.apply {
            val (restoredCardView, restoredTextView) = when (mViewModel.chartSelectedType) {
                is ChartSelectedType.Year -> arrayOf(cardViewYear, textViewYear)
                is ChartSelectedType.Months -> arrayOf(cardViewMonth, textViewMonths)
                is ChartSelectedType.Weeks -> arrayOf(cardViewWeek, textViewWeeks)
                is ChartSelectedType.Days -> arrayOf(cardViewDay, textViewDays)
                is ChartSelectedType.SixMonths -> arrayOf(cardViewHalfYear, textViewSixMonths)
                is ChartSelectedType.All -> return // ChartSelectedType.All is set by default
            }
            switchCardsViewStyle(restoredCardView as CardView, restoredTextView as TextView)
            setSelectedViews(restoredCardView, restoredTextView)
        }
    }

    private fun setSelectedViews(newCard: CardView, newText: TextView) {
        mCurrentActiveCard = newCard
        mCurrentActiveText = newText
    }

    private fun showSuggestion() {
        mViewHelper.runAlphaIn(mBinding!!.includeSuggestion.root, mBinding!!.point)
    }

    private fun hideSuggestion() {
        mViewHelper.runAlphaOut(mBinding!!.includeSuggestion.root, mBinding!!.point)
    }

    private fun switchChartWidgetsVisibility(hasData: Boolean) {
        when {
            hasData && mBinding!!.groupChartWidgets.visibility == View.GONE -> {
                TransitionManager.beginDelayedTransition(mBinding!!.root)
                mBinding!!.groupChartWidgets.visibility = View.VISIBLE
            }
            !hasData && mBinding!!.groupChartWidgets.visibility == View.VISIBLE -> {
                mBinding!!.groupChartWidgets.visibility = View.GONE
            }
        }

        if (hasData) {
            mBinding!!.progressBar.visibility = View.GONE
        }
    }

    private suspend fun prepareToChartDataChanges() {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            if (mBinding!!.includeSuggestion.root.alpha == 1F) {
                withContext(mCoroutineContext.Main) {
                    hideSuggestion()
                }
                delay(150)
            }
        }.join()
    }

    private fun setUpChartWidgetsListeners() {
        mBinding!!.apply {
            cardViewDay.setOnClickListener {
                onCardClicked(it as CardView, textViewDays, ChartSelectedType.Days)
            }
            cardViewWeek.setOnClickListener {
                onCardClicked(it as CardView, textViewWeeks, ChartSelectedType.Weeks)
            }
            cardViewMonth.setOnClickListener {
                onCardClicked(it as CardView, textViewMonths, ChartSelectedType.Months)
            }
            cardViewHalfYear.setOnClickListener {
                onCardClicked(it as CardView, textViewSixMonths, ChartSelectedType.SixMonths)
            }
            cardViewYear.setOnClickListener {
                onCardClicked(it as CardView, textViewYear, ChartSelectedType.Year)
            }
            cardViewAll.setOnClickListener {
                onCardClicked(it as CardView, textViewAll, ChartSelectedType.All)
            }
            chartView.setOnTouchListener {
                mViewModel.onChartClicked(it).also { isNewMarker ->
                    if (isNewMarker) onChartClicked(it)
                }
            }
        }
    }
}