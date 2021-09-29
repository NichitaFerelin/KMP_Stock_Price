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

package com.ferelin.feature_chart.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.ferelin.core.utils.px
import com.ferelin.feature_chart.R
import com.ferelin.feature_chart.utils.points.BezierPoint
import com.ferelin.feature_chart.utils.points.Marker
import com.ferelin.feature_chart.viewData.ChartPastPrices
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
    private var mMarkers: List<Marker> = emptyList()
    private var mBezierMarkers: HashMap<Marker, BezierPoint> = hashMapOf()

    private var mCharHeight: Int = 0
    private var mChartWidth: Int = 0

    private var mMaxValue: Double = 0.0
    private var mMinValue: Double = 0.0

    /*
    * To avoid some graphic bugs when points is too many.
    * */
    private var mTooManyPoints = false
    private var mTooManyPointsMargin = 0F

    /**
     * Chart background color as gradient
     * */
    private val mGradientColors = intArrayOf(
        ContextCompat.getColor(context, R.color.gradientEnd),
        ContextCompat.getColor(context, R.color.gradientStart)
    )

    private val mLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.px.toFloat()
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.black)
    }

    private var mGradientZeroY: Float = 0F
    private var mGradient: LinearGradient? = null
    private val mGradientPath: Path = Path()
    private lateinit var mGradientPaint: Paint
    private val mLinePath: Path = Path()

    private var mZeroY: Float = 0F
    private var mPxPerUnit: Float = 0F

    private var mOnTouchListener: ((marker: Marker) -> Unit)? = null
    private var mLastNearestPoint: Marker? = null

    private var mTouchEventDetector: GestureDetector? =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return if (e != null) {
                    onSingleTapListener(e)
                    true
                } else false
            }
        })

    private var mOnDataPreparedCallback: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        if (mMarkers.isNotEmpty()) {
            drawGradient(canvas)
            drawLine(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mChartWidth = widthSize
        mCharHeight = heightSize

        calcAndInvalidate()
        setMeasuredDimension(mChartWidth, mCharHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mTouchEventDetector?.onTouchEvent(event).also { wasHandled ->
            if (wasHandled == true) performClick()
        } ?: false
    }

    override fun performClick(): Boolean {
        super.performClick()
        mLastNearestPoint?.let { mOnTouchListener?.invoke(it) }
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

        mTooManyPoints = newList.size > 100
        mTooManyPointsMargin =
            if (mTooManyPoints) resources.getDimension(R.dimen.fakePointPadding) else 0F

        mMaxValue = newList.maxByOrNull { it.price }!!.price
        mMinValue = newList.minByOrNull { it.price }!!.price

        val startFakePoint = newList.first().price
        val endFakePoint = newList.last().price

        // Fake points
        newList.add(0, Marker(price = startFakePoint, priceStr = "", date = ""))
        newList.add(Marker(price = endFakePoint, priceStr = "", date = ""))

        if (mTooManyPoints) {
            // two more fake points
            newList.add(0, Marker(price = startFakePoint, priceStr = "", date = ""))
            newList.add(Marker(price = endFakePoint, priceStr = "", date = ""))
        }

        mMarkers = newList
        mLastNearestPoint = null

        calcAndInvalidate()
    }

    fun addOnChartPreparedListener(onDataPrepared: () -> Unit) {
        mOnDataPreparedCallback = onDataPrepared
    }

    fun setOnTouchListener(func: (marker: Marker) -> Unit) {
        mOnTouchListener = func
    }

    fun restoreMarker(previousMarker: Marker): Marker? {
        for (marker in mMarkers) {
            if (marker == previousMarker) {
                return marker
            }
        }
        return null
    }

    private fun calcAndInvalidate() {
        buildGradient()
        if (mMarkers.isNotEmpty()) {
            calculatePositions()
            invalidate()
            mOnDataPreparedCallback?.invoke()
        }
    }

    private fun buildGradient() {
        mGradientPaint = Paint().apply {
            this.style = Paint.Style.FILL
            this.shader = mGradient
            this.isAntiAlias = true
        }

        mGradient = LinearGradient(
            0F,
            mCharHeight - resources.getDimension(R.dimen.chartHeight) - paddingTop.toFloat(),
            0F,
            mCharHeight - paddingTop.toFloat(),
            mGradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawGradient(canvas: Canvas) {
        if (mMarkers.isNotEmpty()) {
            mGradientPath.reset()
            mGradientPath.apply {
                val firstItem = mMarkers.first().position
                moveTo(firstItem.x, mZeroY)
                lineTo(firstItem.x, firstItem.y)

                for (index in 1 until mMarkers.size) {
                    val marker = mMarkers[index]
                    mBezierMarkers[mMarkers[index]]?.let { code ->
                        mGradientPath.cubicTo(
                            code.x1,
                            code.y1,
                            code.x2,
                            code.y2,
                            marker.position.x,
                            marker.position.y
                        )
                    }
                }

                val lastItem = mMarkers.last().position
                lineTo(lastItem.x, mZeroY)

                close()
            }
            canvas.drawPath(mGradientPath, mGradientPaint)
        }
    }

    private fun drawLine(canvas: Canvas) {
        val firstItem = mMarkers.first()
        mLinePath.reset()
        mLinePath.moveTo(firstItem.position.x, firstItem.position.y)

        for (index in 1 until mMarkers.size) {
            val marker = mMarkers[index]
            val bezierPoint = mBezierMarkers[mMarkers[index]]!!
            mLinePath.cubicTo(
                bezierPoint.x1,
                bezierPoint.y1,
                bezierPoint.x2,
                bezierPoint.y2,
                marker.position.x,
                marker.position.y
            )
        }
        canvas.drawPath(mLinePath, mLinePaint)
    }

    private fun calculatePositions() {
        mPxPerUnit =
            ((mCharHeight - paddingTop - paddingBottom) / (mMaxValue - mMinValue)).toFloat()
        mZeroY = mMaxValue.toFloat() * mPxPerUnit + paddingTop
        mGradientZeroY = (mZeroY - (mMinValue - (mMinValue * 5 / 100)) * mPxPerUnit).toFloat()

        val fakePoints = if (mTooManyPoints) 4 else 1
        val step = (mChartWidth) / (mMarkers.size - fakePoints)

        // First fake point
        mMarkers[0].apply {
            position.x = 0F
            position.y = (mZeroY - price * mPxPerUnit).toFloat()
        }

        if (mTooManyPoints) {
            // Second fake point
            mMarkers[1].apply {
                position.x = mTooManyPointsMargin
                position.y = (mZeroY - price * mPxPerUnit).toFloat()

                mBezierMarkers[this] = BezierPoint(
                    x1 = (position.x + mMarkers[0].position.x) / 2,
                    y1 = mMarkers[0].position.y,
                    x2 = (position.x + mMarkers[0].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        val (startIndex, endIndex) = if (mTooManyPoints) {
            intArrayOf(2, mMarkers.size - 2)
        } else intArrayOf(1, mMarkers.size - 1)

        for (index in startIndex until endIndex) {
            val marker = mMarkers[index].apply {
                position.x = (step * index + mTooManyPointsMargin)
                position.y = (mZeroY - price * mPxPerUnit).toFloat()
            }

            mBezierMarkers[mMarkers[index]] = BezierPoint(
                x1 = (marker.position.x + mMarkers[index - 1].position.x) / 2,
                y1 = mMarkers[index - 1].position.y,
                x2 = (marker.position.x + mMarkers[index - 1].position.x) / 2,
                y2 = marker.position.y
            )
        }

        if (mTooManyPoints) {
            // last - 1 fake point
            mMarkers[mMarkers.lastIndex - 1].apply {
                position.x = mChartWidth - mTooManyPointsMargin
                position.y = (mZeroY - price * mPxPerUnit).toFloat()

                mBezierMarkers[this] = BezierPoint(
                    x1 = (position.x + mMarkers[mMarkers.lastIndex - 2].position.x) / 2,
                    y1 = mMarkers[mMarkers.lastIndex - 2].position.y,
                    x2 = (position.x + mMarkers[mMarkers.lastIndex - 2].position.x) / 2,
                    y2 = position.y
                )
            }
        }

        // Last fake point
        mMarkers[mMarkers.lastIndex].apply {
            position.x = mChartWidth.toFloat()
            position.y = (mZeroY - price * mPxPerUnit).toFloat()

            mBezierMarkers[this] = BezierPoint(
                x1 = (position.x + mMarkers[mMarkers.lastIndex - 1].position.x) / 2,
                y1 = mMarkers[mMarkers.lastIndex - 1].position.y,
                x2 = (position.x + mMarkers[mMarkers.lastIndex - 1].position.x) / 2,
                y2 = position.y
            )
        }
    }

    private fun onSingleTapListener(event: MotionEvent) {
        val nearestPoint = findNearestPoint(event)
        event.setLocation(nearestPoint?.position?.x ?: 0F, nearestPoint?.position?.y ?: 0F)
        mLastNearestPoint = nearestPoint
    }

    /**
     * Provides to find nearest point by touched coordinates.
     * The point is searched using only X coordinate.
     *
     * @return nearest point by coordinates
     * */
    private fun findNearestPoint(event: MotionEvent): Marker? {
        var nearestPoint: Marker? = null

        val (startIndex, endIndex) = if (mTooManyPoints) {
            intArrayOf(2, mMarkers.size - 2)
        } else intArrayOf(1, mMarkers.size - 1)

        for (index in startIndex until endIndex) {
            val item = mMarkers[index]
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