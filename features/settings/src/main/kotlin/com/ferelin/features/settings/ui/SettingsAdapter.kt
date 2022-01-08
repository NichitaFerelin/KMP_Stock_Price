package com.ferelin.features.settings.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.ui.R
import com.ferelin.core.ui.view.adapter.createRecyclerAdapter
import com.ferelin.features.settings.databinding.ItemOptionBinding
import com.ferelin.features.settings.databinding.ItemOptionSwitchBinding

internal const val OPTION_VIEW_TYPE = 0
internal const val SWITCH_OPTION_VIEW_TYPE = 1

internal fun createOptionsAdapter(
  onOptionClick: (SettingsViewData) -> Unit
) = createRecyclerAdapter(
  OPTION_VIEW_TYPE,
  ItemOptionBinding::inflate
) { viewBinding, item, _, _ ->
  item as SettingsViewData
  viewBinding.textViewTitle.text = item.title
  viewBinding.textViewSource.text = item.source
  viewBinding.image.setImageResource(item.iconRes)
  viewBinding.image.contentDescription = item.iconContentDescription
  viewBinding.cardHolder.setOnClickListener { onOptionClick.invoke(item) }
}

internal fun createSwitchOptionAdapter(
  onOptionSwitched: (SwitchOptionViewData, Boolean) -> Unit
) = createRecyclerAdapter(
  SWITCH_OPTION_VIEW_TYPE,
  ItemOptionSwitchBinding::inflate
) { viewBinding, item, _, _ ->
  item as SwitchOptionViewData
  viewBinding.textViewTitle.text = item.title
  viewBinding.textViewSource.text = item.source
  viewBinding.switchNotify.isChecked = item.isChecked
  viewBinding.switchNotify.setOnCheckedChangeListener { _, isChecked ->
    onOptionSwitched.invoke(item, isChecked)
  }
}

internal class OptionDecoration(context: Context) : RecyclerView.ItemDecoration() {
  private val margin = context.resources.getDimension(R.dimen.optionBottomMargin).toInt()

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    outRect.bottom = margin
  }
}