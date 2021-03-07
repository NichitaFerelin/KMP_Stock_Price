package com.ferelin.stockprice.ui.common

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
        outRect.left = mContext.resources.getDimension(R.dimen.defaultMargin).toInt()
        outRect.right = mContext.resources.getDimension(R.dimen.defaultMargin).toInt()
        outRect.bottom = mContext.resources.getDimension(R.dimen.smallMargin).toInt()
    }
}