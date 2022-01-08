package com.ferelin.features.settings.ui

import android.content.Context
import com.ferelin.core.ui.R
import com.ferelin.core.ui.view.adapter.ViewDataType

internal enum class OptionType {
  AUTH,
  CLEAR_DATA,
  SOURCE_CODE,
  NOTIFY_PRICE
}

internal object MenuOptionsProvider {
  fun buildMenuOptions(
    context: Context,
    isUserAuthenticated: Boolean,
    isNotifyChecked: Boolean
  ): List<ViewDataType> {
    return listOf(
      SettingsViewData(
        id = 0,
        type = OptionType.AUTH,
        title = context.getString(R.string.titleAuthorization),
        source = context.getString(
          if (isUserAuthenticated) {
            R.string.sourceAuthorized
          } else R.string.sourceNotAuthorized
        ),
        iconRes = if (isUserAuthenticated) {
          R.drawable.ic_logout_30
        } else {
          R.drawable.ic_login_30
        },
        iconContentDescription = if (isUserAuthenticated) {
          context.getString(R.string.descriptionLogOut)
        } else {
          context.getString(R.string.descriptionLogIn)
        }
      ),
      SwitchOptionViewData(
        id = 1,
        type = OptionType.NOTIFY_PRICE,
        title = context.getString(R.string.titleNotifyPriceUpdates),
        source = context.getString(R.string.sourceNotify),
        isChecked = isNotifyChecked
      ),
      SettingsViewData(
        id = 2,
        type = OptionType.SOURCE_CODE,
        title = context.getString(R.string.titleSourceCode),
        source = context.getString(R.string.sourceDownload),
        iconRes = R.drawable.ic_download_30,
        iconContentDescription = context.getString(R.string.descriptionDownload)
      ),
      SettingsViewData(
        id = 3,
        type = OptionType.CLEAR_DATA,
        title = context.getString(R.string.titleClearData),
        source = context.getString(R.string.sourceClearData),
        iconRes = R.drawable.ic_delete_30,
        iconContentDescription = context.getString(R.string.descriptionDelete)
      )
    )
  }
}