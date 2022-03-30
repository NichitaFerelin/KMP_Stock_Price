package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.mapper.CompanyMapper
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.repository.CompanyRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class CompanyRepositoryImpl @Inject constructor(
  private val companyDao: CompanyDao,
  private val profileDao: ProfileDao,
  private val jsonSource: CompanyJsonSource
) : CompanyRepository {
  override val companies: Observable<List<Company>>
    get() = companyDao.getAll()
      .distinctUntilChanged()
      .map { it.map(CompanyMapper::map) }
      .doOnEach { dbCompaniesNotification ->
        val dbCompanies = dbCompaniesNotification.value ?: emptyList()
        if (dbCompanies.isEmpty()) {
          val jsonData = jsonSource.parseJson().unzip()

          companyDao.insertAll(jsonData.first)
          profileDao.insertAll(jsonData.second)
        }
      }
}