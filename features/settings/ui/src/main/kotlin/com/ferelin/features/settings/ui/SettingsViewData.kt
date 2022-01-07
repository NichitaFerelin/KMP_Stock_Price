package com.ferelin.features.settings.ui

import com.ferelin.core.ui.view.adapter.ViewDataType

internal data class SettingsViewData(
  val id: Long,
  val type: OptionType,
  val title: String,
  val source: String,
  val iconRes: Int,
  val iconContentDescription: String
) : ViewDataType(OPTION_VIEW_TYPE) {
  override fun getUniqueId(): Long = id
}

internal data class SwitchOptionViewData(
  val id: Long,
  val type: OptionType,
  val title: String,
  val source: String,
  val isChecked: Boolean
) : ViewDataType(SWITCH_OPTION_VIEW_TYPE) {
  override fun getUniqueId(): Long = id
}