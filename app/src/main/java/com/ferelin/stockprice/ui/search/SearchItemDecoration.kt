package com.ferelin.stockprice.ui.search

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ferelin.stockprice.R

class SearchItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var mNextRow = false
    private val mFirstItemStartMargin = context.resources.getDimension(R.dimen.defaultMargin).toInt()
    private val mDefaultStartMargin = context.resources.getDimension(R.dimen.small).toInt()
    private val mSecondRowTopMargin = context.resources.getDimension(R.dimen.smallMargin).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        when {
            parent.getChildAdapterPosition(view) == 0 -> {
                outRect.left = mFirstItemStartMargin

            }
            parent.getChildAdapterPosition(view) == 1 -> outRect.left = mFirstItemStartMargin
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex == 0 -> {
                outRect.left = mDefaultStartMargin
            }
            else -> outRect.left = mDefaultStartMargin
        }


        /*  if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1) {
              outRect.left = mFirstItemStartMargin
              //outRect.top = mSecondRowTopMargin
          } else {
              outRect.bottom = mSecondRowTopMargin
              outRect.left = mFirstItemStartMargin
          }*/

        /*val column = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        when {
            parent.getChildAdapterPosition(view) == 0 ||  -> {
                outRect.left = mFirstItemStartMargin
                mNextRow = false
            }
            column == 0 -> outRect.left = mDefaultStartMargin
            mNextRow -> {
                outRect.left = mDefaultStartMargin
                outRect.top = mSecondRowTopMargin
            }
            else -> {
                mNextRow = true
                outRect.left = mFirstItemStartMargin
                outRect.top = mSecondRowTopMargin
            }
        }*/
    }
}