package com.ferelin.stockprice.shared.commonMain.data.mapper

import com.ferelin.stockprice.db.ProfileDBO
import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.Profile

internal object ProfileMapper {
  fun map(profile: Profile): ProfileDBO {
    return ProfileDBO(
      id = profile.id.value,
      country = profile.country,
      phone = profile.phone,
      webUrl = profile.webUrl,
      industry = profile.industry,
      capitalization = profile.capitalization
    )
  }

  fun map(profileDBO: ProfileDBO): Profile {
    return Profile(
      id = CompanyId(profileDBO.id),
      country = profileDBO.country,
      phone = profileDBO.phone,
      webUrl = profileDBO.webUrl,
      industry = profileDBO.industry,
      capitalization = profileDBO.capitalization
    )
  }
}