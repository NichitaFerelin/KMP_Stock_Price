package com.ferelin.stockprice.custom.utils

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

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ferelin.stockprice.R

/**
 * [SuggestionControlHelper] providing a method to control and display the SuggestionView on Chart.
 */
object SuggestionControlHelper {

    fun applyCoordinatesChanges(
        rootSuggestionView: ConstraintLayout,
        pointView: View,
        plugView: View,
        arrowView: View,
        relativeView: View,
        marker: Marker
    ) {
        val (pointX, pointY) = calculatePointAbsoluteCoordinates(
            rootSuggestionView.context,
            marker,
            relativeView.left,
            relativeView.top
        )
        val (suggestionX, suggestionY) = calculateMarkerAbsoluteCoordinates(
            rootSuggestionView.context,
            pointX,
            pointY
        )

        val (finalSuggestionX, finalSuggestionY) = detectAndFixOutOfBorderOffsets(
            rootSuggestionView.context,
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