package com.ferelin.features.settings.ui

import android.content.Context
import com.ferelin.core.ui.R

internal enum class OptionType {
  AUTH,
  LOG_OUT,
  CLEAR_DATA,
  SOURCE_CODE,
  NOTIFY_PRICE
}

internal object MenuOptionsProvider {
  fun defaultSettings(context: Context): List<SettingsViewData> {
    return listOf(
      SettingsViewData(
        id = DOWNLOAD_INDEX,
        type = OptionType.SOURCE_CODE,
        title = context.getString(R.string.titleSourceCode),
        source = context.getString(R.string.sourceDownload),
        iconRes = R.drawable.ic_download_30,
        iconContentDescription = context.getString(R.string.descriptionDownload)
      ),
      SettingsViewData(
        id = ERASE_INDEX,
        type = OptionType.CLEAR_DATA,
        title = context.getString(R.string.titleClearData),
        source = context.getString(R.string.sourceClearData),
        iconRes = R.drawable.ic_delete_30,
        iconContentDescription = context.getString(R.string.descriptionDelete)
      )
    )
  }

  fun userAuthenticated(context: Context, isUserAuthenticated: Boolean): SettingsViewData {
    return SettingsViewData(
      id = AUTH_INDEX,
      type = if (isUserAuthenticated) OptionType.LOG_OUT else OptionType.AUTH,
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
    )
  }
}

internal const val DOWNLOAD_INDEX = 0L
internal const val ERASE_INDEX = 1L
internal const val AUTH_INDEX = 2L