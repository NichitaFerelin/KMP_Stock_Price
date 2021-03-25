package com.ferelin.stockprice.custom.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ferelin.stockprice.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SuggestionControlHelper {

    suspend fun applyCoordinatesChanges(
        context: Context,
        rootSuggestionView: ConstraintLayout,
        pointView: View,
        plugView: View,
        arrowView: View,
        relativeView: View,
        marker: Marker
    ) {
        val (pointX, pointY) = calculatePointAbsoluteCoordinates(
            context,
            marker,
            relativeView.left,
            relativeView.top
        )
        val (suggestionX, suggestionY) = calculateMarkerAbsoluteCoordinates(
            context,
            pointX,
            pointY
        )

        withContext(Dispatchers.Main) {
            val (finalSuggestionX, finalSuggestionY) = detectAndFixOutOfBorderOffsets(
                context,
                rootSuggestionView,
                plugView,
                arrowView,
                relativeView,
                suggestionX,
                suggestionY
            )

            applyCoordinates(
                pointView,
                rootSuggestionView,
                pointX,
                pointY,
                finalSuggestionX,
                finalSuggestionY
            )
        }
    }

    private fun applyCoordinates(
        pointView: View,
        rootSuggestion: ConstraintLayout,
        pointX: Float,
        pointY: Float,
        suggestionX: Float,
        suggestionY: Float
    ) {
        pointView.x = pointX
        pointView.y = pointY
        rootSuggestion.x = suggestionX
        rootSuggestion.y = suggestionY
    }

    private fun calculatePointAbsoluteCoordinates(
        context: Context,
        marker: Marker,
        relativeViewLeftBorder: Int,
        relativeViewTopBorder: Int
    ): FloatArray {
        val markerPointX = marker.position.x
        val markerPointY = marker.position.y

        val pointRadius = context.resources.getDimension(R.dimen.pointWidth) / 2
        val pointX = markerPointX - relativeViewLeftBorder - pointRadius
        val pointY = markerPointY + relativeViewTopBorder - pointRadius
        return floatArrayOf(pointX, pointY)
    }

    private fun calculateMarkerAbsoluteCoordinates(
        context: Context,
        pointX: Float,
        pointY: Float
    ): FloatArray {
        val pointRadius = context.resources.getDimension(R.dimen.pointWidth) / 2
        val suggestionLayoutWidth = context.resources.getDimension(R.dimen.suggestionLayoutWidth)
        val suggestionLayoutHalfWidth = suggestionLayoutWidth / 2
        val suggestionLayoutHeight = context.resources.getDimension(R.dimen.suggestionLayoutHeight)
        val offsetFromPoint = context.resources.getDimension(R.dimen.suggestionOffsetFromPoint)

        val suggestionX = pointX - suggestionLayoutHalfWidth + pointRadius
        val suggestionY = pointY - suggestionLayoutHeight - offsetFromPoint
        return floatArrayOf(suggestionX, suggestionY)
    }

    private fun detectAndFixOutOfBorderOffsets(
        context: Context,
        root: ConstraintLayout,
        plugView: View,
        arrowView: View,
        relativeView: View,
        suggestionX: Float,
        suggestionY: Float
    ): FloatArray {
        val leftBorder = relativeView.left
        val rightBorder = relativeView.right
        val topBorder = relativeView.top
        val bottomBorder = relativeView.bottom

        val suggestionLayoutWidth = context.resources.getDimension(R.dimen.suggestionLayoutWidth)
        val suggestionLayoutHeight = context.resources.getDimension(R.dimen.suggestionLayoutHeight)

        val pointHeight = context.resources.getDimension(R.dimen.pointHeight)
        val offsetFromPoint = context.resources.getDimension(R.dimen.suggestionOffsetFromPoint)

        val outOfBorderOffset = suggestionLayoutWidth / 2

        val (finalSuggestionX, finalSuggestionY) = when {
            // ok
            suggestionX > leftBorder
                    && (suggestionX + suggestionLayoutWidth) < rightBorder
                    && suggestionY > topBorder
                    && (suggestionY + suggestionLayoutHeight) < bottomBorder -> {
                changeSuggestionArrowConstrainsToBottom(root, plugView, arrowView)
                floatArrayOf(suggestionX, suggestionY)
            }

            suggestionX < leftBorder
                    && suggestionY < topBorder -> {
                // move to right-bottom
                val newX = suggestionX + outOfBorderOffset
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, newY)
            }
            (suggestionX + suggestionLayoutWidth) > rightBorder
                    && suggestionY < topBorder -> {
                // move to left-bottom
                val newX = suggestionX - outOfBorderOffset
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, newY)
            }
            suggestionX < leftBorder
                    && (suggestionY + suggestionLayoutHeight) > bottomBorder -> {
                // move to right-top
                val newX = suggestionX + outOfBorderOffset
                val newY = suggestionY - outOfBorderOffset
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, newY)
            }
            (suggestionX + suggestionLayoutWidth) > rightBorder
                    && suggestionY > topBorder -> {
                // move to left-top
                val newX = suggestionX - outOfBorderOffset
                val newY = suggestionY - outOfBorderOffset
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, newY)
            }
            suggestionY < topBorder -> {
                // move to bottom
                val newY =
                    suggestionY + offsetFromPoint + pointHeight + suggestionLayoutHeight + offsetFromPoint
                changeSuggestionArrowConstraintsToTop(root, plugView, arrowView)
                floatArrayOf(suggestionX, newY)
            }
            (suggestionY + suggestionLayoutHeight) > bottomBorder -> {
                // move to top
                val newY = suggestionY - outOfBorderOffset
                changeSuggestionArrowConstrainsToBottom(root, plugView, arrowView)
                floatArrayOf(suggestionX, newY)
            }
            suggestionX < leftBorder -> {
                // move to right
                val newX = suggestionX + outOfBorderOffset
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, suggestionY)
            }
            (suggestionX + suggestionLayoutWidth) > rightBorder -> {
                // move to left
                val newX = suggestionX - outOfBorderOffset
                hideSuggestionAttributes(plugView, arrowView)
                floatArrayOf(newX, suggestionY)
            }
            else -> throw IllegalStateException(
                "Unchecked case for suggestion coordinates" +
                        "suggestionX: $suggestionX, suggestionY: $suggestionY"
            )
        }

        return floatArrayOf(finalSuggestionX, finalSuggestionY)
    }

    private fun changeSuggestionArrowConstraintsToTop(
        root: ConstraintLayout,
        plug: View,
        arrow: View
    ) {
        showSuggestionAttributes(plug, arrow)
        arrow.rotationX = 180F
        ConstraintSet().apply {
            clone(root)
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
            applyTo(root)
        }
    }

    private fun changeSuggestionArrowConstrainsToBottom(
        root: ConstraintLayout,
        plug: View,
        arrow: View
    ) {
        showSuggestionAttributes(plug, arrow)
        arrow.rotationX = 0F
        ConstraintSet().apply {
            clone(root)
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
            applyTo(root)
        }
    }

    private fun hideSuggestionAttributes(plug: View, arrow: View) {
        plug.visibility = View.GONE
        arrow.visibility = View.GONE
    }

    private fun showSuggestionAttributes(plug: View, arrow: View) {
        plug.visibility = View.VISIBLE
        arrow.visibility = View.VISIBLE
    }
}