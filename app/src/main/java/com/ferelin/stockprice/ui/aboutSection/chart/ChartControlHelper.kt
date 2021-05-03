package com.ferelin.stockprice.ui.aboutSection.chart

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.ferelin.stockprice.databinding.FragmentChartBinding

interface ChartControlHelper {

    fun getAttachedViewsByMode(
        viewBinding: FragmentChartBinding,
        mode: ChartViewMode
    ): Array<View> {
        with(viewBinding) {
            return when (mode) {
                is ChartViewMode.Year -> arrayOf(cardViewYear, textViewYear)
                is ChartViewMode.Months -> arrayOf(cardViewMonth, textViewMonths)
                is ChartViewMode.Weeks -> arrayOf(cardViewWeek, textViewWeeks)
                is ChartViewMode.Days -> arrayOf(cardViewDay, textViewDays)
                is ChartViewMode.SixMonths -> arrayOf(cardViewHalfYear, textViewSixMonths)
                is ChartViewMode.All -> arrayOf(cardViewAll, textViewAll)
            }
        }
    }

    fun getCardAttachedViewMode(viewBinding: FragmentChartBinding, card: CardView): ChartViewMode {
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

    fun getCardAttachedTextView(viewBinding: FragmentChartBinding, card: CardView): TextView {
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