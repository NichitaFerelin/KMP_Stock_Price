package com.ferelin.core.ui.params

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartParams(
  val companyId: Int,
  val companyTicker: String
) : Parcelable