package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.shared.data.entity.company.CompanyDao
import com.ferelin.stockprice.shared.data.entity.company.CompanyJsonSource
import com.ferelin.stockprice.shared.data.entity.profile.ProfileDao
import com.ferelin.stockprice.shared.data.mapper.CompanyMapper
import com.ferelin.stockprice.shared.domain.entity.Company
import com.ferelin.stockprice.shared.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CompanyRepositoryImpl(
  private val companyDao: CompanyDao,
  private val profileDao: ProfileDao,
  private val jsonSource: CompanyJsonSource
) : CompanyRepository {
  override val companies: Flow<List<Company>>
    get() = companyDao.getAll()
      .distinctUntilChanged()
      .map { it.map(CompanyMapper::map) }
      .onEach { dbCompanies ->
        if (dbCompanies.isEmpty()) {
          val jsonData = jsonSource.parseJson().unzip()
          companyDao.insertAll(jsonData.first)
          profileDao.insertAll(jsonData.second)
        }
      }
}