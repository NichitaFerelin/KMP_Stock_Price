package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.androidApp.domain.entity.Company
import com.ferelin.stockprice.androidApp.domain.entity.Profile
import com.ferelin.stockprice.androidApp.ui.toStrPrice
import com.ferelin.stockprice.androidApp.ui.viewData.ProfileViewData

object ProfileMapper {
  fun map(profile: Profile, company: Company): ProfileViewData {
    return ProfileViewData(
      companyName = company.name,
      logoUrl = company.logoUrl,
      country = profile.country,
      phone = profile.phone,
      webUrl = profile.webUrl,
      industry = profile.industry,
      capitalization = profile.capitalization.toDouble().toStrPrice()
    )
  }
}