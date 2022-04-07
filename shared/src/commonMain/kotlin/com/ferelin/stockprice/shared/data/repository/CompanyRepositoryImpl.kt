package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.shared.data.entity.company.CompanyApi
import com.ferelin.stockprice.shared.data.entity.company.CompanyDao
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
  private val companyApi: CompanyApi
) : CompanyRepository {
  override val companies: Flow<List<Company>>
    get() = companyDao.getAll()
      .distinctUntilChanged()
      .map { it.map(CompanyMapper::map) }
      .onEach { dbCompanies ->
        if (dbCompanies.isEmpty()) {
          val apiCompanies = companyApi.load()
          val resultData = CompanyMapper.map(apiCompanies)
          companyDao.insertAll(resultData.first)
          profileDao.insertAll(resultData.second)
        }
      }
}