package com.ferelin.stockprice.utils.swipe

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import kotlin.math.abs
import kotlin.math.ln

class SwipeActionCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private var mRebounded = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = Float.MAX_VALUE

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = Float.MAX_VALUE

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        (recyclerView.adapter as StocksRecyclerAdapter).onUntouched(viewHolder as StockViewHolder,mRebounded)
        mRebounded = false
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder !is StockViewHolder) {
            return
        }

        val itemView = viewHolder.itemView
        val currentSwipePercentage = abs(dX) / itemView.width

        if (currentSwipePercentage > 0.2F && !mRebounded) {
            (recyclerView.adapter as StocksRecyclerAdapter).onRebound(viewHolder)
            mRebounded = true
        }
        translateReboundingView(itemView, viewHolder, dX)
    }

    private fun translateReboundingView(
        itemView: View,
        viewHolder: StockViewHolder,
        dX: Float
    ) {
        val swipeDismissDistanceHorizontal = itemView.width * 0.5F
        val dragFraction = ln(
            (1 + (dX / swipeDismissDistanceHorizontal)).toDouble()
        ) / ln(
            3.toDouble()
        )
        val dragTo = dragFraction * swipeDismissDistanceHorizontal * 0.8F
        viewHolder.binding.rootLayout.translationX = dragTo.toFloat()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Do nothing.
    }
}