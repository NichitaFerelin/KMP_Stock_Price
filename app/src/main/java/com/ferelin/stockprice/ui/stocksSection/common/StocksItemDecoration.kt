package com.ferelin.stockprice.ui.stocksSection.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.R

class StocksItemDecoration(private val mContext: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        when (parent.adapter?.getItemViewType(position)) {
            StocksRecyclerAdapter.ITEM_STOCK_TYPE -> {
                outRect.bottom = mContext.resources.getDimension(R.dimen.stockItemBottomMargin).toInt()
            }
            StocksRecyclerAdapter.ITEM_TEXT_TYPE -> {
                outRect.left =
                    mContext.resources.getDimension(R.dimen.textDividerStartMargin).toInt()
                outRect.top =
                    mContext.resources.getDimension(R.dimen.textDividerTopMargin).toInt()
                outRect.bottom = mContext.resources.getDimension(R.dimen.stockItemBottomMargin).toInt()
            }
        }

    }
}