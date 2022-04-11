package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.shared.domain.entity.Company
import com.ferelin.stockprice.shared.domain.entity.Profile
import com.ferelin.stockprice.shared.ui.toStrPrice
import com.ferelin.stockprice.shared.ui.viewData.ProfileViewData

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