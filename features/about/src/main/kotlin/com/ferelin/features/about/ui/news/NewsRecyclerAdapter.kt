package com.ferelin.features.about.ui.news

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.ui.view.adapter.createRecyclerAdapter
import com.ferelin.features.about.R
import com.ferelin.features.about.databinding.ItemNewsBinding

internal const val NEWS_VIEW_TYPE = 0

internal fun createNewsAdapter(
  onItemClick: (NewsViewData) -> Unit
) = createRecyclerAdapter(
  NEWS_VIEW_TYPE,
  ItemNewsBinding::inflate
) { viewBinding, item, _, _ ->

  with(viewBinding) {
    item as NewsViewData

    textViewSource.text = item.source
    textViewDate.text = item.date
    textViewHeadline.text = item.headline
    textViewSummary.text = item.summary
    textViewUrl.text = item.sourceUrl

    textViewUrl.setOnClickListener {
      onItemClick.invoke(item)
    }
  }
}

internal class NewsItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
  private val margin = context.resources.getDimension(R.dimen.about_news_item_offset).toInt()

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.top = margin
    }
    outRect.bottom = margin
    outRect.right = margin
    outRect.left = margin
  }
}