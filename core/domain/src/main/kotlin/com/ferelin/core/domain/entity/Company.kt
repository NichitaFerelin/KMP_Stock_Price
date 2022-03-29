package com.ferelin.core.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Company(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String
)

@Parcelize
data class CompanyId(val value: Int) : Parcelable