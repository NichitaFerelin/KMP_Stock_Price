package com.ferelin.stockprice.ui.aboutSection.news

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.R

class NewsItemDecoration(private val mContext: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mContext.resources.getDimension(R.dimen.newsItemOffset).toInt()
        }
        outRect.bottom = mContext.resources.getDimension(R.dimen.newsItemOffset).toInt()
        outRect.right = mContext.resources.getDimension(R.dimen.newsItemOffset).toInt()
        outRect.left = mContext.resources.getDimension(R.dimen.newsItemOffset).toInt()
    }
}