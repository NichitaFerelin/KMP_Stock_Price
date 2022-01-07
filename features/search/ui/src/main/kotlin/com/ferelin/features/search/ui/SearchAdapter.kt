package com.ferelin.features.search.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.ui.view.adapter.createRecyclerAdapter
import com.ferelin.features.search.ui.databinding.ItemTickerBinding

internal const val TICKER_VIEW_TYPE = 0

internal fun createTickerAdapter(
  onTickerClick: (SearchViewData) -> Unit
) = createRecyclerAdapter(
  TICKER_VIEW_TYPE,
  ItemTickerBinding::inflate
) { viewBinding, item, _, _ ->
  item as SearchViewData
  viewBinding.textViewName.text = item.text
  viewBinding.root.setOnClickListener { onTickerClick.invoke(item) }
}

internal open class SearchItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
  private val firstItemStartMargin =
    context.resources.getDimension(R.dimen.searchItemStartMargin).toInt()
  private val defaultStartMargin =
    context.resources.getDimension(R.dimen.searchItemMargin).toInt()
  open val twoRows: Boolean = true

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    val finalChildCounter = parent.adapter?.itemCount ?: 0
    when {
      parent.getChildAdapterPosition(view) == 0 -> {
        outRect.left = firstItemStartMargin
      }
      twoRows && parent.getChildAdapterPosition(view) == 1 -> {
        outRect.left = firstItemStartMargin
      }
      parent.getChildAdapterPosition(view) == finalChildCounter - 1 -> {
        addMarginToLastItem(outRect)
      }
      twoRows && parent.getChildAdapterPosition(view) == finalChildCounter - 2 -> {
        addMarginToLastItem(outRect)
      }
      else -> outRect.left = defaultStartMargin
    }
  }

  private fun addMarginToLastItem(outRect: Rect) {
    outRect.left = defaultStartMargin
    outRect.right = firstItemStartMargin
  }
}

internal class SearchItemDecorationLandscape(context: Context) : SearchItemDecoration(context) {
  override val twoRows: Boolean = false
}