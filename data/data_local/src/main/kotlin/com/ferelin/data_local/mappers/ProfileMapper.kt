/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.data_local.mappers

import com.ferelin.domain.entities.Profile
import com.ferelin.data_local.entities.ProfileDBO
import com.ferelin.data_local.utils.CompanyPojo
import javax.inject.Inject

class ProfileMapper @Inject constructor() {

    fun map(profile: Profile): ProfileDBO {
        return ProfileDBO(
            relationCompanyId = profile.relationCompanyId,
            country = profile.country,
            phone = profile.phone,
            webUrl = profile.webUrl,
            industry = profile.industry,
            currency = profile.currency,
            capitalization = profile.capitalization
        )
    }

    fun map(profileDBO: ProfileDBO): Profile {
        return Profile(
            relationCompanyId = profileDBO.relationCompanyId,
            country = profileDBO.country,
            phone = profileDBO.phone,
            webUrl = profileDBO.webUrl,
            industry = profileDBO.industry,
            currency = profileDBO.currency,
            capitalization = profileDBO.capitalization
        )
    }

    fun map(index: Int, companyPojo: CompanyPojo) : Profile {
        return Profile(
            relationCompanyId = index,
            country = companyPojo.country,
            phone = companyPojo.phone,
            webUrl = companyPojo.weburl,
            industry = companyPojo.finnhubIndustry,
            currency = companyPojo.currency,
            capitalization = companyPojo.marketCapitalization
        )
    }
}