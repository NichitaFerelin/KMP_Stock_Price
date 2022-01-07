package com.ferelin.core.ui.view.stocks

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.ui.view.stocks.adapter.StockViewHolder
import kotlin.math.abs
import kotlin.math.ln

class StockSwipeActionCallback(
  val onHolderRebound: (StockViewHolder) -> Unit,
  val onHolderUntouched: (StockViewHolder, Boolean) -> Unit,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
  override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = Float.MAX_VALUE
  override fun getSwipeVelocityThreshold(defaultValue: Float): Float = Float.MAX_VALUE
  override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE
  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

  private var rebounded = false

  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean = false

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    if (viewHolder is StockViewHolder) {
      onHolderUntouched(viewHolder, rebounded)
      rebounded = false
    }
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

    if (currentSwipePercentage > 0.2F && !rebounded) {
      onHolderRebound(viewHolder)
      rebounded = true
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
    val dragTo = dragFraction * swipeDismissDistanceHorizontal * 1.2F
    viewHolder.viewBinding.rootLayout.translationX = dragTo.toFloat()
  }
}