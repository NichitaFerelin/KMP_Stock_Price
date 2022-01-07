package com.ferelin.core.data

import com.ferelin.core.data.entity.ProfileDao
import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.mapper.CompanyMapper
import com.ferelin.core.domain.entities.entity.Company
import com.ferelin.core.domain.entities.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class CompanyRepositoryImpl @Inject constructor(
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