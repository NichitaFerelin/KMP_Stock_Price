package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.databinding.FragmentChartBinding
import com.ferelin.stockprice.utils.showSnackbar
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartFragment(owner: AdaptiveCompany? = null) : BaseFragment<ChartViewModel>() {

    private lateinit var mBinding: FragmentChartBinding

    private lateinit var mPreviousActiveCard: CardView
    private lateinit var mPreviousActiveText: TextView

    override val mViewModel: ChartViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, owner)
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
        switchSelectedType()

        mBinding.apply {
            cardViewDay.setOnClickListener {
                onCardClicked(it as CardView, textViewDays)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.Days)
            }
            cardViewWeek.setOnClickListener {
                onCardClicked(it as CardView, textViewWeeks)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.Weeks)
            }
            cardViewMonth.setOnClickListener {
                onCardClicked(it as CardView, textViewMonths)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.Months)
            }
            cardViewHalfYear.setOnClickListener {
                onCardClicked(it as CardView, textViewSixMonths)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.SixMonths)
            }
            cardViewYear.setOnClickListener {
                onCardClicked(it as CardView, textViewYear)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.Year)
            }
            cardViewAll.setOnClickListener {
                onCardClicked(it as CardView, textViewAll)
                mViewModel.onChartControlButtonClicked(ChartSelectedType.All)
            }

            chartView.setOnTouchListener {
                mViewModel.onChartClicked(it).also { isNewMarker ->
                    if (isNewMarker) {
                        onChartClicked(it)
                    }
                }
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {

            launch {
                mViewModel.hasDataForChartState.collect { hasData ->
                    Log.d("Test", "Collect: $hasData")
                    withContext(mCoroutineContext.Main) {
                        if (hasData && mBinding.groupChartWidgets.visibility == View.GONE) {
                            Log.d("Test", "Set visible")
                            TransitionManager.beginDelayedTransition(mBinding.root)
                            mBinding.groupChartWidgets.visibility = View.VISIBLE
                        } else if (!hasData && mBinding.groupChartWidgets.visibility == View.VISIBLE) {
                            Log.d("Test", "set hide")
                            mBinding.groupChartWidgets.visibility = View.GONE
                        }

                        if (hasData) {
                            mBinding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }


            launch {
                mViewModel.eventDataChanged.collect {
                    withContext(mCoroutineContext.Main) {
                        mBinding.apply {
                            val animation = AnimatorInflater.loadAnimator(
                                requireContext(),
                                R.animator.scale_in_out
                            )
                            animation.setTarget(mBinding.textViewCurrentPrice)
                            animation.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator?) {
                                    textViewCurrentPrice.text = mViewModel.currentPrice
                                    textViewBuyPrice.text = String.format(
                                        resources.getString(R.string.buy_for),
                                        mViewModel.currentPrice
                                    )
                                    textViewDayProfit.text = mViewModel.dayProfit
                                    textViewDayProfit.setTextColor(mViewModel.profitBackground)
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                            })
                            animation.start()
                        }
                    }
                }
            }

            launch {
                mViewModel.eventStockHistoryChanged.collect {
                    withContext(mCoroutineContext.Main) {
                        mBinding.chartView.setData(it)
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showSnackbar(mBinding.root, it)
                    }
                }
            }
        }
    }

    private fun onChartClicked(marker: Marker) {
        if (mBinding.includeSuggestion.root.alpha == 0.0F) {
            val (suggestionX, suggestionY) = calculateMarkerAbsoluteCoordinates(marker)
            detectAndFixOutOfBorderOffsets(suggestionX, suggestionY)
            updateSuggestionText(marker)
            showSuggestion()
        } else {
            viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                val (suggestionX, suggestionY) = calculateMarkerAbsoluteCoordinates(marker)
                withContext(mCoroutineContext.Main) {
                    hideSuggestion()
                }
                delay(150)
                withContext(mCoroutineContext.Main) {
                    detectAndFixOutOfBorderOffsets(suggestionX, suggestionY)
                    updateSuggestionText(marker)
                    showSuggestion()
                }
            }
        }

    }

    // TODO
    private var newPointX = 0F
    private var newPointY = 0F

    private fun calculateMarkerAbsoluteCoordinates(marker: Marker): FloatArray {
        val markerPointX = marker.position.x
        val markerPointY = marker.position.y

        val chartViewLeftBorder = mBinding.chartView.left
        val chartViewTopBorder = mBinding.chartView.top

        val pointRadius = resources.getDimension(R.dimen.pointWidth) / 2
        val pointX = markerPointX - chartViewLeftBorder - pointRadius
        val pointY = markerPointY + chartViewTopBorder - pointRadius
        newPointX = pointX
        newPointY = pointY

        val suggestionLayoutWidth = resources.getDimension(R.dimen.suggestionLayoutWidth)
        val suggestionLayoutHalfWidth = suggestionLayoutWidth / 2
        val suggestionLayoutHeight = resources.getDimension(R.dimen.suggestionLayoutHeight)
        val offsetFromPoint = resources.getDimension(R.dimen.suggestionOffsetFromPoint)

        val suggestionX = pointX - suggestionLayoutHalfWidth + pointRadius
        val suggestionY = pointY - suggestionLayoutHeight - offsetFromPoint
        return floatArrayOf(suggestionX, suggestionY)
    }

    private fun detectAndFixOutOfBorderOffsets(suggestionX: Float, suggestionY: Float) {
        val chartViewBottomBorder = mBinding.chartView.bottom
        val chartViewRightBorder = mBinding.chartView.right
        val chartViewLeftBorder = mBinding.chartView.left
        val chartViewTopBorder = mBinding.chartView.top

        val suggestionLayoutWidth = resources.getDimension(R.dimen.suggestionLayoutWidth)
        val suggestionLayoutHeight = resources.getDimension(R.dimen.suggestionLayoutHeight)

        val pointHeight = resources.getDimension(R.dimen.pointHeight)
        val offsetFromPoint = resources.getDimension(R.dimen.suggestionOffsetFromPoint)

        val outOfBorderOffset = suggestionLayoutWidth / 2

        val (finalSuggestionX, finalSuggestionY) = when {
            // ok
            suggestionX > chartViewLeftBorder
                    && (suggestionX + suggestionLayoutWidth) < chartViewRightBorder
                    && suggestionY > chartViewTopBorder
                    && (suggestionY + suggestionLayoutHeight) < chartViewBottomBorder -> {
                changeSuggestionArrowConstrainsToBottom()
                floatArrayOf(suggestionX, suggestionY)
            }

            suggestionX < chartViewLeftBorder
                    && suggestionY < chartViewTopBorder -> {
                // move to right-bottom
                val newX = suggestionX + outOfBorderOffset
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                hideSuggestionAttributes()
                floatArrayOf(newX, newY)
            }
            (suggestionX + suggestionLayoutWidth) > chartViewRightBorder
                    && suggestionY < chartViewTopBorder -> {
                // move to left-bottom
                val newX = suggestionX - outOfBorderOffset
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                hideSuggestionAttributes()
                floatArrayOf(newX, newY)
            }
            suggestionX < chartViewLeftBorder
                    && (suggestionY + suggestionLayoutHeight) > chartViewBottomBorder -> {
                // move to right-top
                val newX = suggestionX + outOfBorderOffset
                val newY = suggestionY - outOfBorderOffset
                hideSuggestionAttributes()
                floatArrayOf(newX, newY)
            }
            (suggestionX + suggestionLayoutWidth) > chartViewRightBorder
                    && suggestionY > chartViewTopBorder -> {
                // move to left-top
                val newX = suggestionX - outOfBorderOffset
                val newY = suggestionY - outOfBorderOffset
                hideSuggestionAttributes()
                floatArrayOf(newX, newY)
            }
            suggestionY < chartViewTopBorder -> {
                // move to bottom
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                changeSuggestionArrowConstraintsToTop()
                floatArrayOf(suggestionX, newY)
            }
            (suggestionY + suggestionLayoutHeight) > chartViewBottomBorder -> {
                // move to top
                val newY = suggestionY - outOfBorderOffset
                changeSuggestionArrowConstrainsToBottom()
                floatArrayOf(suggestionX, newY)
            }
            suggestionX < chartViewLeftBorder -> {
                // move to rigth
                val newX = suggestionX + outOfBorderOffset
                hideSuggestionAttributes()
                floatArrayOf(newX, suggestionY)
            }
            (suggestionX + suggestionLayoutWidth) > chartViewRightBorder -> {
                // move to left
                val newX = suggestionX - outOfBorderOffset
                hideSuggestionAttributes()
                floatArrayOf(newX, suggestionY)
            }
            else -> throw IllegalStateException(
                "Unchecked case for suggestion coordinates" +
                        "suggestionX: $suggestionX, suggestionY: $suggestionY"
            )
        }

        mBinding.point.x = newPointX
        mBinding.point.y = newPointY
        mBinding.includeSuggestion.root.x = finalSuggestionX
        mBinding.includeSuggestion.root.y = finalSuggestionY
    }

    private fun updateSuggestionText(marker: Marker) {
        mBinding.includeSuggestion.textViewDate.text = marker.date
        mBinding.includeSuggestion.textViewPrice.text = marker.priceStr
    }

    private fun onCardClicked(card: CardView, attachedTextView: TextView) {
        if (card != mPreviousActiveCard) {

            hideSuggestion()

            val animation = AnimatorInflater.loadAnimator(
                requireContext(),
                R.animator.scale_in_out
            )
            animation.setTarget(card)
            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    switchCardViewStyles(card, attachedTextView)
                }

                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
            animation.start()
        } else {
            val animation = AnimatorInflater.loadAnimator(
                requireContext(),
                R.animator.scale_in_out
            )
            animation.setTarget(card)
            animation.start()
        }
    }

    private fun switchCardViewStyles(card: CardView, attachedTextView: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        attachedTextView.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.whiteDark)
        )
        mPreviousActiveCard.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.whiteDark)
        )
        mPreviousActiveText.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.black)
        )
        mPreviousActiveCard = card
        mPreviousActiveText = attachedTextView
    }

    private fun switchSelectedType() {
        mBinding.apply {
            when (mViewModel.chartSelectedType) {
                is ChartSelectedType.All -> switchPreviousViews(cardViewAll, textViewAll)
                is ChartSelectedType.Year -> switchPreviousViews(cardViewYear, textViewYear)
                is ChartSelectedType.Months -> switchPreviousViews(cardViewMonth, textViewMonths)
                is ChartSelectedType.Weeks -> switchPreviousViews(cardViewWeek, textViewWeeks)
                is ChartSelectedType.Days -> switchPreviousViews(cardViewDay, textViewDays)
                is ChartSelectedType.SixMonths -> switchPreviousViews(
                    cardViewHalfYear,
                    textViewSixMonths
                )
            }
        }
    }

    private fun switchPreviousViews(newCard: CardView, newText: TextView) {
        mPreviousActiveCard = newCard
        mPreviousActiveText = newText
    }

    private fun showSuggestion() {
        mBinding.includeSuggestion.root.animate().alpha(1F).duration = 150L
        mBinding.point.animate().alpha(1F).duration = 150L
    }

    private fun hideSuggestion() {
        mBinding.includeSuggestion.root.animate().alpha(0.0F).duration = 150L
        mBinding.point.animate().alpha(0.0F).duration = 150L
    }

    private fun hideSuggestionAttributes() {
        mBinding.includeSuggestion.viewArrow.visibility = View.GONE
        mBinding.includeSuggestion.viewPlug.visibility = View.GONE
    }

    private fun showSuggestionAttributes() {
        mBinding.includeSuggestion.viewArrow.visibility = View.VISIBLE
        mBinding.includeSuggestion.viewPlug.visibility = View.VISIBLE
    }

    private fun changeSuggestionArrowConstraintsToTop() {
        showSuggestionAttributes()
        mBinding.includeSuggestion.viewArrow.rotationX = 180F
        ConstraintSet().apply {
            clone(mBinding.includeSuggestion.root)
            clear(R.id.viewArrow, ConstraintSet.TOP)
            clear(R.id.viewPlug, ConstraintSet.TOP)
            connect(
                R.id.viewArrow,
                ConstraintSet.BOTTOM,
                R.id.cardViewSuggestion,
                ConstraintSet.TOP
            )
            connect(
                R.id.viewPlug,
                ConstraintSet.BOTTOM,
                R.id.cardViewSuggestion,
                ConstraintSet.TOP
            )
            applyTo(mBinding.includeSuggestion.root)
        }
    }

    private fun changeSuggestionArrowConstrainsToBottom() {
        showSuggestionAttributes()
        mBinding.includeSuggestion.viewArrow.rotationX = 0F
        ConstraintSet().apply {
            clone(mBinding.includeSuggestion.root)
            clear(R.id.viewArrow, ConstraintSet.BOTTOM)
            clear(R.id.viewPlug, ConstraintSet.BOTTOM)
            connect(
                R.id.viewArrow,
                ConstraintSet.TOP,
                R.id.cardViewSuggestion,
                ConstraintSet.BOTTOM
            )
            connect(
                R.id.viewPlug,
                ConstraintSet.TOP,
                R.id.cardViewSuggestion,
                ConstraintSet.BOTTOM
            )
            applyTo(mBinding.includeSuggestion.root)
        }
    }
}