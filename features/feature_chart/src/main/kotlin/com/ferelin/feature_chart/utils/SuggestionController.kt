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

package com.ferelin.feature_chart.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ferelin.feature_chart.utils.points.Marker
import com.ferelin.feature_chart.view.ChartView
import com.ferelin.core.R as coreRes
import com.ferelin.feature_chart.R as chartRes

/**
 * [SuggestionController] provides a method to control and display the SuggestionView at Chart.
 * @see [ChartView]
 */
object SuggestionController {

    /**
     * When user clicks on the ChartView, it returns coordinates of marker that were clicked. From
     * this coordinates it it necessary to set SuggestionView. This method make it correct avoiding
     * off-screen or any other problems.
     *
     * @param rootSuggestionView is a suggestion view that must be set correct
     * @param pointView is a point tied to suggestion view
     * @param plugView is a view that plugs a space between arrow and suggestion
     * @param arrowView is a arrow tied to suggestion view
     * @param relativeView is a view to which the coordinates need to be aligned
     * @param marker is a marker with coordinates of clicked point
     * */
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

        val pointRadius = context.resources.getDimension(coreRes.dimen.pointWidth) / 2
        val pointX = markerPointX - relativeViewLeftBorder - pointRadius
        val pointY = markerPointY + relativeViewTopBorder - pointRadius
        return floatArrayOf(pointX, pointY)
    }

    private fun calculateMarkerAbsoluteCoordinates(
        context: Context,
        pointX: Float,
        pointY: Float
    ): FloatArray {
        val pointRadius = context.resources.getDimension(coreRes.dimen.pointWidth) / 2
        val suggestionLayoutWidth =
            context.resources.getDimension(coreRes.dimen.suggestionLayoutWidth)
        val suggestionLayoutHalfWidth = suggestionLayoutWidth / 2
        val suggestionLayoutHeight =
            context.resources.getDimension(coreRes.dimen.suggestionLayoutHeight)
        val offsetFromPoint =
            context.resources.getDimension(coreRes.dimen.suggestionOffsetFromPoint)

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

        val suggestionLayoutWidth =
            context.resources.getDimension(coreRes.dimen.suggestionLayoutWidth)
        val suggestionLayoutHeight =
            context.resources.getDimension(coreRes.dimen.suggestionLayoutHeight)

        val pointHeight = context.resources.getDimension(coreRes.dimen.pointHeight)
        val offsetFromPoint =
            context.resources.getDimension(coreRes.dimen.suggestionOffsetFromPoint)

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
            clear(chartRes.id.viewArrow, ConstraintSet.TOP)
            clear(chartRes.id.viewPlug, ConstraintSet.TOP)
            connect(
                chartRes.id.viewArrow,
                ConstraintSet.BOTTOM,
                chartRes.id.cardViewSuggestion,
                ConstraintSet.TOP
            )
            connect(
                chartRes.id.viewPlug,
                ConstraintSet.BOTTOM,
                chartRes.id.cardViewSuggestion,
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
            clear(chartRes.id.viewArrow, ConstraintSet.BOTTOM)
            clear(chartRes.id.viewPlug, ConstraintSet.BOTTOM)
            connect(
                chartRes.id.viewArrow,
                ConstraintSet.TOP,
                chartRes.id.cardViewSuggestion,
                ConstraintSet.BOTTOM
            )
            connect(
                chartRes.id.viewPlug,
                ConstraintSet.TOP,
                chartRes.id.cardViewSuggestion,
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