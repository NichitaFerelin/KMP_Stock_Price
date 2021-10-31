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

package com.ferelin.core.customView.chart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.ferelin.core.R
import com.ferelin.core.customView.chart.points.BezierPoint
import com.ferelin.core.customView.chart.points.Marker
import com.ferelin.core.customView.chart.utils.ChartAttrs
import com.ferelin.core.customView.chart.utils.SuggestionAttrs
import kotlin.math.abs


/**
 * [ChartView] provides the ability to display data that has fields like date and number.
 * For example. class A, with fields:
 *  - Price = 100$, Date = 12.04.18
 *
 * - Chart is built by plotting Bezier points.
 * - The width of the chart is calculated by the number of points.
 * - The height of the points is calculated from the average of all input data.
 */
class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val superStateKey = "super"
        const val markersKey = "markers"
        const val lastPointKey = "last"
    }

    // Base markers to build Bezier curve.
    private var markers: List<Marker> = emptyList()
    private var bezierMarkers: HashMap<Marker, BezierPoint> = hashMapOf()

    private var lastSelectedMarker: Marker? = null

    private lateinit var chartAttrs: ChartAttrs
    private lateinit var suggestionAttrs: SuggestionAttrs

    private var chartHeight: Int = 0
    private var chartWidth: Int = 0

    private var maxPointValue: Double = 0.0
    private var minPointValue: Double = 0.0

    /*
    * To avoid some graphic bugs when points is too many.
    * */
    private var tooManyPoints = false
    private var tooManyPointsMargin = 0F

    private var zeroY: Float = 0F
    private var pxPerUnit: Float = 0F

    private var touchEventDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onSingleTapUp(event: MotionEvent?): Boolean {
                return if (event != null) {
                    onSingleTap(event)
                    true
                } else {
                    false
                }
            }
        })

    init {
        applyAttributes(
            attrs = context.theme.obtainStyledAttributes(attrs, R.styleable.ChartView, 0, 0)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        chartWidth = widthSize
        chartHeight = heightSize

        calcAndInvalidate()
        setMeasuredDimension(chartWidth, chartHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (markers.isNotEmpty()) {
            drawGradient(canvas)
            drawLine(canvas)
            drawSuggestion(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchEventDetector.onTouchEvent(event).also { wasHandled ->
            if (wasHandled) {
                performClick()
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()

        if (lastSelectedMarker != null) {
            invalidate()
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(superStateKey, super.onSaveInstanceState())
            putParcelableArrayList(markersKey, ArrayList(markers))
            putParcelable(lastPointKey, lastSelectedMarker)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var savedState = state
        savedState?.let {
            if (it is Bundle) {
                savedState = it.getParcelable(superStateKey)
                markers = it.getParcelableArrayList(markersKey) ?: emptyList()
                lastSelectedMarker = it.getParcelable(lastPointKey)
            }
        }

        super.onRestoreInstanceState(savedState)
    }

    fun setData(history: ChartPastPrices) {
        if (history.prices.isEmpty()) {
            return
        }

        /**
         * Converts input history to markers
         * */
        val newList = mutableListOf<Marker>()
        for (index in history.prices.indices) {
            newList.add(
                Marker(
                    price = history.prices[index],
                    priceStr = history.pricesStr[index],
                    date = history.dates[index]
                )
            )
        }

        tooManyPoints = newList.size > 100
        tooManyPointsMargin =
            if (tooManyPoints) resources.getDimension(R.dimen.chartPointPadding) else 0F

        maxPointValue = newList.maxByOrNull { it.price }!!.price
        minPointValue = newList.minByOrNull { it.price }!!.price

        val startFakePoint = newList.first().price
        val endFakePoint = newList.last().price

        // Fake points
        newList.add(0, Marker(price = startFakePoint, priceStr = "", date = ""))
        newList.add(Marker(price = endFakePoint, priceStr = "", date = ""))

        if (tooManyPoints) {
            // two more fake points
            newList.add(0, Marker(price = startFakePoint, priceStr = "", date = ""))
            newList.add(Marker(price = endFakePoint, priceStr = "", date = ""))
        }

        markers = newList
        lastSelectedMarker = null

        calcAndInvalidate()
    }

    private fun calcAndInvalidate() {
        buildGradient()
        if (markers.isNotEmpty()) {
            calculatePositions()
            invalidate()
        }
    }

    private fun buildGradient() {
        chartAttrs.gradientPaint = Paint().apply {
            this.style = Paint.Style.FILL
            this.shader = chartAttrs.gradient
            this.isAntiAlias = true
        }

        chartAttrs.gradient = LinearGradient(
            0F,
            chartHeight - resources.getDimension(R.dimen.chartHeight) - paddingTop.toFloat(),
            0F,
            chartHeight - paddingTop.toFloat(),
            chartAttrs.gradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawGradient(canvas: Canvas) {
        with(chartAttrs) {
            if (markers.isNotEmpty()) {
                gradientPath.reset()
                gradientPath.apply {
                    val firstItem = markers.first().position
                    moveTo(firstItem.x, zeroY)
                    lineTo(firstItem.x, firstItem.y)

                    for (index in 1 until markers.size) {
                        val marker = markers[index]
                        bezierMarkers[markers[index]]?.let { code ->
                            gradientPath.cubicTo(
                                code.x1,
                                code.y1,
                                code.x2,
                                code.y2,
                                marker.position.x,
                                marker.position.y
                            )
                        }
                    }

                    val lastItem = markers.last().position
                    lineTo(lastItem.x, zeroY)

                    close()
                }
                gradientPaint?.let { canvas.drawPath(gradientPath, it) }
            }
        }
    }

    private fun drawLine(canvas: Canvas) {
        with(chartAttrs) {
            val firstItem = markers.first()
            linePath.reset()
            linePath.moveTo(firstItem.position.x, firstItem.position.y)

            for (index in 1 until markers.size) {
                val marker = markers[index]
                val bezierPoint = bezierMarkers[markers[index]]!!
                linePath.cubicTo(
                    bezierPoint.x1,
                    bezierPoint.y1,
                    bezierPoint.x2,
                    bezierPoint.y2,
                    marker.position.x,
                    marker.position.y
                )
            }
            canvas.drawPath(linePath, linePaint)
        }
    }

    private fun drawSuggestion(canvas: Canvas) {
        lastSelectedMarker?.let {
            drawPointAt(canvas, it)
            drawSuggestionRelativeTo(canvas, it)
        }
    }

    private fun drawPointAt(canvas: Canvas, lastPoint: Marker) {
        canvas.drawCircle(
            lastPoint.position.x,
            lastPoint.position.y,
            suggestionAttrs.mainPointRadius,
            suggestionAttrs.mainPointPaint
        )

        canvas.drawCircle(
            lastPoint.position.x,
            lastPoint.position.y,
            suggestionAttrs.subPointRadius,
            suggestionAttrs.subPointPaint
        )
    }

    private fun drawSuggestionRelativeTo(canvas: Canvas, lastPoint: Marker) {
        with(suggestionAttrs) {
            val (suggestionStartX, suggestionStartY) = getSuggestionStartCoords(lastPoint)

            canvas.drawRoundRect(
                suggestionStartX,
                suggestionStartY,
                suggestionStartX + suggestionWidth,
                suggestionStartY + suggestionHeight,
                suggestionRectRadius,
                suggestionRectRadius,
                boardPaint
            )

            val priceToDraw = lastPoint.priceStr
            val dateToDraw = lastPoint.date

            val tempBounds = Rect()

            pricePaint.getTextBounds(priceToDraw, 0, priceToDraw.lastIndex, tempBounds)
            val priceHeight = tempBounds.height()
            val priceWidth = tempBounds.width()

            datePaint.getTextBounds(dateToDraw, 0, dateToDraw.lastIndex, tempBounds)
            val dateHeight = tempBounds.height()
            val dateWidth = tempBounds.width()

            val priceX = suggestionStartX + (suggestionWidth / 2 - priceWidth / 2) - 3F
            val priceY = suggestionStartY +
                    suggestionHeight / 2 - suggestionMarginBetween / 2 - priceHeight + dateHeight

            val dateX = suggestionStartX + (suggestionWidth / 2 - dateWidth / 2) - 2F
            val dateY = priceY + priceHeight + suggestionMarginBetween

            canvas.drawText(priceToDraw, priceX, priceY, pricePaint)
            canvas.drawText(dateToDraw, dateX, dateY, datePaint)
        }
    }

    private fun calculatePositions() {
        pxPerUnit =
            ((chartHeight - paddingTop - paddingBottom) / (maxPointValue - minPointValue)).toFloat()
        zeroY = maxPointValue.toFloat() * pxPerUnit + paddingTop
        chartAttrs.gradientZeroY =
            (zeroY - (minPointValue - (minPointValue * 5 / 100)) * pxPerUnit).toFloat()

        val fakePoints = if (tooManyPoints) 4 else 1
        val step = (chartWidth) / (markers.size - fakePoints)

        // First fake point
        markers[0].apply {
            position.x = 0F
            position.y = (zeroY - price * pxPerUnit).toFloat()
        }

        if (tooManyPoints) {
            // Second fake point
            markers[1].apply {
                position.x = tooManyPointsMargin
                position.y = (zeroY - price * pxPerUnit).toFloat()

                bezierMarkers[this] = BezierPoint(
                    x1 = (position.x + markers[0].position.x) / 2,
                    y1 = markers[0].position.y,
                    x2 = (position.x + markers[0].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        val (startIndex, endIndex) = if (tooManyPoints) {
            intArrayOf(2, markers.size - 2)
        } else intArrayOf(1, markers.size - 1)

        for (index in startIndex until endIndex) {
            val marker = markers[index].apply {
                position.x = (step * index + tooManyPointsMargin)
                position.y = (zeroY - price * pxPerUnit).toFloat()
            }

            bezierMarkers[markers[index]] = BezierPoint(
                x1 = (marker.position.x + markers[index - 1].position.x) / 2,
                y1 = markers[index - 1].position.y,
                x2 = (marker.position.x + markers[index - 1].position.x) / 2,
                y2 = marker.position.y
            )
        }

        if (tooManyPoints) {
            // last - 1 fake point
            markers[markers.lastIndex - 1].apply {
                position.x = chartWidth - tooManyPointsMargin
                position.y = (zeroY - price * pxPerUnit).toFloat()

                bezierMarkers[this] = BezierPoint(
                    x1 = (position.x + markers[markers.lastIndex - 2].position.x) / 2,
                    y1 = markers[markers.lastIndex - 2].position.y,
                    x2 = (position.x + markers[markers.lastIndex - 2].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        // Last fake point
        markers[markers.lastIndex].apply {
            position.x = chartWidth.toFloat()
            position.y = (zeroY - price * pxPerUnit).toFloat()

            bezierMarkers[this] = BezierPoint(
                x1 = (position.x + markers[markers.lastIndex - 1].position.x) / 2,
                y1 = markers[markers.lastIndex - 1].position.y,
                x2 = (position.x + markers[markers.lastIndex - 1].position.x) / 2,
                y2 = position.y
            )
        }
    }

    private fun onSingleTap(event: MotionEvent) {
        val nearestPoint = findNearestPoint(event)
        event.setLocation(nearestPoint?.position?.x ?: 0F, nearestPoint?.position?.y ?: 0F)
        lastSelectedMarker = nearestPoint
    }

    /**
     * Finds nearest point by touch coordinates
     * @return nearest point [Marker]
     * */
    private fun findNearestPoint(event: MotionEvent): Marker? {
        var nearestPoint: Marker? = null

        val (startIndex, endIndex) = if (tooManyPoints) {
            intArrayOf(2, markers.size - 2)
        } else {
            intArrayOf(1, markers.size - 1)
        }

        for (index in startIndex until endIndex) {
            val item = markers[index]
            val itemPosition = item.position
            if (
                nearestPoint == null ||
                abs(event.x - itemPosition.x) < abs(event.x - nearestPoint.position.x)
            ) {
                nearestPoint = item
            }
        }

        return nearestPoint
    }

    /**
     * Calculates suggestion position by [Marker] except situations
     * when the suggestion view can be out of the screen
     * @return [FloatArray] with X and Y coords
     * */
    private fun getSuggestionStartCoords(lastPoint: Marker): FloatArray {
        val pointX = lastPoint.position.x
        val pointY = lastPoint.position.y

        with(suggestionAttrs) {
            return when {

                // to top
                pointY - offsetFromPoint - suggestionHeight > 0
                        && pointX - suggestionWidth / 2 > 0
                        && pointX + suggestionWidth / 2 < this@ChartView.width -> {

                    val x = pointX - (suggestionWidth / 2)
                    val y = pointY - offsetFromPoint - suggestionHeight
                    floatArrayOf(x, y)
                }

                // to bottom
                pointY + offsetFromPoint + suggestionHeight < this@ChartView.height
                        && pointX - suggestionWidth / 2 > 0
                        && pointX + suggestionWidth / 2 < this@ChartView.width -> {

                    val x = pointX - (suggestionWidth / 2)
                    val y = pointY + offsetFromPoint
                    floatArrayOf(x, y)
                }

                // to top-right
                pointY - offsetFromPoint - suggestionHeight > 0
                        && pointX + offsetFromPoint + suggestionWidth < this@ChartView.width -> {

                    val x = pointX + offsetFromPoint
                    val y = pointY - offsetFromPoint - suggestionHeight
                    floatArrayOf(x, y)
                }

                // to top-left
                pointY - offsetFromPoint - suggestionHeight > 0
                        && pointX - offsetFromPoint - suggestionWidth > this@ChartView.x -> {

                    val x = pointX - offsetFromPoint - suggestionWidth
                    val y = pointY - offsetFromPoint - suggestionHeight
                    floatArrayOf(x, y)
                }

                // to bottom-right
                pointY + offsetFromPoint + suggestionHeight < this@ChartView.height
                        && pointX + offsetFromPoint + suggestionWidth < this@ChartView.width -> {

                    val x = pointX + offsetFromPoint
                    val y = pointY + offsetFromPoint
                    floatArrayOf(x, y)
                }

                // to bottom-left
                pointY + offsetFromPoint + suggestionHeight < this@ChartView.height
                        && pointX - offsetFromPoint - suggestionWidth > 0 -> {

                    val x = pointX - offsetFromPoint - suggestionWidth
                    val y = pointY + offsetFromPoint
                    floatArrayOf(x, y)
                }

                else -> floatArrayOf(0F, 0F)
            }
        }
    }

    private fun applyAttributes(attrs: TypedArray) {
        attrs.apply {
            try {
                val chartBackgroundGradientStart =
                    getInteger(R.styleable.ChartView_chartBackgroundGradientStart, 0)
                val chartBackgroundGradientEnd =
                    getInteger(R.styleable.ChartView_chartBackgroundGradientEnd, 0)
                val chartLineColor = getInteger(R.styleable.ChartView_chartLineColor, 0)
                val suggestionBackgroundColor =
                    getInteger(R.styleable.ChartView_suggestionBackgroundColor, 0)
                val suggestionWidth = getDimension(R.styleable.ChartView_suggestionWidth, 0F)
                val suggestionHeight = getDimension(R.styleable.ChartView_suggestionHeight, 0F)
                val suggestionRectRadius =
                    getDimension(R.styleable.ChartView_suggestionRectRadius, 0F)
                val suggestionPriceColor =
                    getInteger(R.styleable.ChartView_suggestionPriceColor, 0)
                val suggestionDateColor =
                    getInteger(R.styleable.ChartView_suggestionDateColor, 0)

                chartAttrs = ChartAttrs(
                    chartBackgroundGradientStart,
                    chartBackgroundGradientEnd,
                    chartLineColor
                )

                suggestionAttrs = SuggestionAttrs(
                    context,
                    suggestionWidth,
                    suggestionHeight,
                    suggestionRectRadius,
                    suggestionBackgroundColor,
                    suggestionPriceColor,
                    suggestionDateColor
                )
            } finally {
                recycle()
            }
        }
    }
}