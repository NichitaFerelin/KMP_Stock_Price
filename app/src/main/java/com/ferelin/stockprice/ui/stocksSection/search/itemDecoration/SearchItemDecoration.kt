package com.ferelin.stockprice.ui.stocksSection.search.itemDecoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.stocksSection.search.SearchRequestsAdapter

open class SearchItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val mFirstItemStartMargin =
        context.resources.getDimension(R.dimen.searchItemStartMargin).toInt()
    private val mDefaultStartMargin =
        context.resources.getDimension(R.dimen.searchItemMargin).toInt()

    open val twoColumns: Boolean = true

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val finalChildCounter = (parent.adapter as SearchRequestsAdapter).itemCount

        when {
            parent.getChildAdapterPosition(view) == 0 -> outRect.left = mFirstItemStartMargin

            twoColumns && parent.getChildAdapterPosition(view) == 1 -> {
                outRect.left = mFirstItemStartMargin
            }

            parent.getChildAdapterPosition(view) == finalChildCounter - 1 -> {
                addMarginToLastItem(outRect)
            }

            twoColumns && parent.getChildAdapterPosition(view) == finalChildCounter - 2 -> {
                addMarginToLastItem(outRect)
            }
            else -> outRect.left = mDefaultStartMargin
        }
    }

    private fun addMarginToLastItem(outRect: Rect) {
        outRect.left = mDefaultStartMargin
        outRect.right = mFirstItemStartMargin
    }
}