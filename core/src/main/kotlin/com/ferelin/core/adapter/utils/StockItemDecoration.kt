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

package com.ferelin.core.adapter.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.R
import com.ferelin.core.adapter.ITEM_STOCK_TYPE
import com.ferelin.core.adapter.ITEM_TEXT_TYPE

class StockItemDecoration(private val mContext: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        when (parent.adapter?.getItemViewType(position)) {
            ITEM_STOCK_TYPE -> {
                outRect.bottom =
                    mContext.resources.getDimension(R.dimen.stockItemBottomMargin).toInt()
                outRect.left = mContext.resources.getDimension(R.dimen.stockItemStartMargin).toInt()
                outRect.right = mContext.resources.getDimension(R.dimen.stockItemEndMargin).toInt()
            }
            ITEM_TEXT_TYPE -> {
                outRect.left =
                    mContext.resources.getDimension(R.dimen.textDividerStartMargin).toInt()
                outRect.top =
                    mContext.resources.getDimension(R.dimen.textDividerTopMargin).toInt()
                outRect.bottom =
                    mContext.resources.getDimension(R.dimen.stockItemBottomMargin).toInt()
            }
        }
    }
}