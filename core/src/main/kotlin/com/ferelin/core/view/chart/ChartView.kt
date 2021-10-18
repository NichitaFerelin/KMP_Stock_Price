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

package com.ferelin.core.view.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.ferelin.core.R
import com.ferelin.core.utils.px
import com.ferelin.core.view.chart.points.BezierPoint
import com.ferelin.core.view.chart.points.Marker
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

    /*
    * Base markers to build Bezier curve.
    * */
    private var markerks: List<Marker> = emptyList()
    private var bezierMarkers: HashMap<Marker, BezierPoint> = hashMapOf()

    private var chartHeight: Int = 0
    private var chartWidth: Int = 0

    private var maxValue: Double = 0.0
    private var minValue: Double = 0.0

    /*
    * To avoid some graphic bugs when points is too many.
    * */
    private var tooManyPoints = false
    private var tooManyPointsMargin = 0F

    /**
     * Chart background color as gradient
     * */
    private val gradientColors = intArrayOf(
        ContextCompat.getColor(context, R.color.gradientEnd),
        ContextCompat.getColor(context, R.color.gradientStart)
    )

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.px.toFloat()
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.black)
    }
    private val linePath: Path = Path()

    private var gradient: LinearGradient? = null
    private var gradientZeroY: Float = 0F
    private val gradientPath: Path = Path()
    private var gradientPaint: Paint? = null

    private var zeroY: Float = 0F
    private var pxPerUnit: Float = 0F

    private var onTouchListener: ((marker: Marker) -> Unit)? = null
    private var lastNearestPoint: Marker? = null

    private var touchEventDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return if (e != null) {
                    onSingleTap(e)
                    true
                } else false
            }
        })

    private var onDataPreparedCallback: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        if (markerks.isNotEmpty()) {
            drawGradient(canvas)
            drawLine(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        chartWidth = widthSize
        chartHeight = heightSize

        calcAndInvalidate()
        setMeasuredDimension(chartWidth, chartHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchEventDetector.onTouchEvent(event).also { wasHandled ->
            if (wasHandled) performClick()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        lastNearestPoint?.let { onTouchListener?.invoke(it) }
        return true
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
            if (tooManyPoints) resources.getDimension(R.dimen.fakePointPadding) else 0F

        maxValue = newList.maxByOrNull { it.price }!!.price
        minValue = newList.minByOrNull { it.price }!!.price

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

        markerks = newList
        lastNearestPoint = null

        calcAndInvalidate()
    }

    fun addOnChartPreparedListener(onDataPrepared: () -> Unit) {
        onDataPreparedCallback = onDataPrepared
    }

    fun setOnTouchListener(func: (marker: Marker) -> Unit) {
        onTouchListener = func
    }

    fun restoreMarker(previousMarker: Marker): Marker? {
        for (marker in markerks) {
            if (marker == previousMarker) {
                return marker
            }
        }
        return null
    }

    private fun calcAndInvalidate() {
        buildGradient()
        if (markerks.isNotEmpty()) {
            calculatePositions()
            invalidate()
            onDataPreparedCallback?.invoke()
        }
    }

    private fun buildGradient() {
        gradientPaint = Paint().apply {
            this.style = Paint.Style.FILL
            this.shader = gradient
            this.isAntiAlias = true
        }

        gradient = LinearGradient(
            0F,
            chartHeight - resources.getDimension(R.dimen.chartHeight) - paddingTop.toFloat(),
            0F,
            chartHeight - paddingTop.toFloat(),
            gradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawGradient(canvas: Canvas) {
        if (markerks.isNotEmpty()) {
            gradientPath.reset()
            gradientPath.apply {
                val firstItem = markerks.first().position
                moveTo(firstItem.x, zeroY)
                lineTo(firstItem.x, firstItem.y)

                for (index in 1 until markerks.size) {
                    val marker = markerks[index]
                    bezierMarkers[markerks[index]]?.let { code ->
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

                val lastItem = markerks.last().position
                lineTo(lastItem.x, zeroY)

                close()
            }
            gradientPaint?.let { canvas.drawPath(gradientPath, it) }
        }
    }

    private fun drawLine(canvas: Canvas) {
        val firstItem = markerks.first()
        linePath.reset()
        linePath.moveTo(firstItem.position.x, firstItem.position.y)

        for (index in 1 until markerks.size) {
            val marker = markerks[index]
            val bezierPoint = bezierMarkers[markerks[index]]!!
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

    private fun calculatePositions() {
        pxPerUnit =
            ((chartHeight - paddingTop - paddingBottom) / (maxValue - minValue)).toFloat()
        zeroY = maxValue.toFloat() * pxPerUnit + paddingTop
        gradientZeroY = (zeroY - (minValue - (minValue * 5 / 100)) * pxPerUnit).toFloat()

        val fakePoints = if (tooManyPoints) 4 else 1
        val step = (chartWidth) / (markerks.size - fakePoints)

        // First fake point
        markerks[0].apply {
            position.x = 0F
            position.y = (zeroY - price * pxPerUnit).toFloat()
        }

        if (tooManyPoints) {
            // Second fake point
            markerks[1].apply {
                position.x = tooManyPointsMargin
                position.y = (zeroY - price * pxPerUnit).toFloat()

                bezierMarkers[this] = BezierPoint(
                    x1 = (position.x + markerks[0].position.x) / 2,
                    y1 = markerks[0].position.y,
                    x2 = (position.x + markerks[0].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        val (startIndex, endIndex) = if (tooManyPoints) {
            intArrayOf(2, markerks.size - 2)
        } else intArrayOf(1, markerks.size - 1)

        for (index in startIndex until endIndex) {
            val marker = markerks[index].apply {
                position.x = (step * index + tooManyPointsMargin)
                position.y = (zeroY - price * pxPerUnit).toFloat()
            }

            bezierMarkers[markerks[index]] = BezierPoint(
                x1 = (marker.position.x + markerks[index - 1].position.x) / 2,
                y1 = markerks[index - 1].position.y,
                x2 = (marker.position.x + markerks[index - 1].position.x) / 2,
                y2 = marker.position.y
            )
        }

        if (tooManyPoints) {
            // last - 1 fake point
            markerks[markerks.lastIndex - 1].apply {
                position.x = chartWidth - tooManyPointsMargin
                position.y = (zeroY - price * pxPerUnit).toFloat()

                bezierMarkers[this] = BezierPoint(
                    x1 = (position.x + markerks[markerks.lastIndex - 2].position.x) / 2,
                    y1 = markerks[markerks.lastIndex - 2].position.y,
                    x2 = (position.x + markerks[markerks.lastIndex - 2].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        // Last fake point
        markerks[markerks.lastIndex].apply {
            position.x = chartWidth.toFloat()
            position.y = (zeroY - price * pxPerUnit).toFloat()

            bezierMarkers[this] = BezierPoint(
                x1 = (position.x + markerks[markerks.lastIndex - 1].position.x) / 2,
                y1 = markerks[markerks.lastIndex - 1].position.y,
                x2 = (position.x + markerks[markerks.lastIndex - 1].position.x) / 2,
                y2 = position.y
            )
        }
    }

    private fun onSingleTap(event: MotionEvent) {
        val nearestPoint = findNearestPoint(event)
        event.setLocation(nearestPoint?.position?.x ?: 0F, nearestPoint?.position?.y ?: 0F)
        lastNearestPoint = nearestPoint
    }

    /**
     * Provides to find nearest point by touched coordinates.
     * The point is searched using only X coordinate.
     *
     * @return nearest point by coordinates
     * */
    private fun findNearestPoint(event: MotionEvent): Marker? {
        var nearestPoint: Marker? = null

        val (startIndex, endIndex) = if (tooManyPoints) {
            intArrayOf(2, markerks.size - 2)
        } else {
            intArrayOf(1, markerks.size - 1)
        }

        for (index in startIndex until endIndex) {
            val item = markerks[index]
            val itemPosition = item.position
            if (
                nearestPoint == null || abs(event.x - itemPosition.x)
                < abs(event.x - nearestPoint.position.x)
            ) {
                nearestPoint = item
            }
        }

        return nearestPoint
    }
}