package com.ferelin.core.ui.params

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AboutParams(
  val companyId: Int,
  val companyTicker: String,
  val companyName: String
) : Parcelable