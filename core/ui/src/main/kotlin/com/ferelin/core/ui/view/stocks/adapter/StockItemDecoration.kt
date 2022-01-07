package com.ferelin.core.ui.view.stocks.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.NULL_INDEX
import com.ferelin.core.ui.R

class StockItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
  private val bottomMargin = context.resources.getDimension(R.dimen.stockBottomMargin).toInt()
  private val startMargin = context.resources.getDimension(R.dimen.stockStartMargin).toInt()
  private val endMargin = context.resources.getDimension(R.dimen.stockEndMargin).toInt()

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    val position = parent.getChildAdapterPosition(view)
    when {
      position == NULL_INDEX -> outRect.applyForStock()
      parent.adapter?.getItemViewType(position) == STOCK_VIEW_TYPE -> outRect.applyForStock()
    }
  }

  private fun Rect.applyForStock() {
    bottom = bottomMargin
    left = startMargin
    right = endMargin
  }
}