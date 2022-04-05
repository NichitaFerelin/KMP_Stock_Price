package com.ferelin.features.about.profile

import com.ferelin.core.ui.viewData.utils.toStrPrice
import com.ferelin.stockprice.domain.entity.Company
import com.ferelin.stockprice.domain.entity.Profile

internal object ProfileMapper {
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