package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.mapper.CompanyMapper
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.FavoriteCompany
import com.ferelin.core.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CompanyRepositoryImpl(
    private val companyDao: CompanyDao,
    private val jsonSource: CompanyJsonSource
) : CompanyRepository {
    override val companies: Flow<List<Company>>
        get() = companyDao.getAll()
            .distinctUntilChanged()
            .map { it.map(CompanyMapper::map) }
            .onEach { dbCompanies ->
                if (dbCompanies.isEmpty()) {
                    val jsonData = jsonSource.parseJson()
                    companyDao.insertAll(jsonData)
                }
            }

    override val favoriteCompanies: Flow<List<FavoriteCompany>>
        get() = companyDao.getAllFavorites()
            .distinctUntilChanged()
            .map { it.map(CompanyMapper::map) }

    override suspend fun addToFavorites(id: CompanyId) {
        companyDao.addToFavorites(id.value)
    }

    override suspend fun eraseFromFavorites(id: CompanyId) {
        companyDao.eraseFromFavorites(id.value)
    }
}