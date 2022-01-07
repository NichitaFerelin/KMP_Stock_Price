package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.ProfileDBO
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.Profile

object ProfileMapper {
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