package com.ferelin.core.data

import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.mapper.FavouriteCompanyMapper
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.repository.FavouriteCompanyRepository
import com.ferelin.core.itemsNotIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class FavouriteCompanyRepositoryImpl @Inject constructor(
  private val dao: FavouriteCompanyDao,
  private val api: FavouriteCompanyApi,
  private val firebaseAuth: FirebaseAuth,
) : FavouriteCompanyRepository {
  override val favouriteCompanies: Flow<List<CompanyId>>
    get() = api.load(firebaseAuth.uid ?: "")
      .combine(
        flow = dao.getAll(),
        transform = { apiCompaniesResponse, dbCompaniesDBO ->
          val apiCompanies = FavouriteCompanyMapper.map(apiCompaniesResponse)
          val dbCompanies = dbCompaniesDBO.map(FavouriteCompanyMapper::map)
          syncData(apiCompanies, dbCompanies)
          dbCompanies
        }
      )

  override suspend fun addToFavourite(companyId: CompanyId) {
    checkBackgroundThread()
    dao.insert(FavouriteCompanyMapper.map(companyId))
    firebaseAuth.uid?.let { userToken ->
      api.putBy(userToken, companyId.value)
    }
  }

  override suspend fun removeFromFavourite(companyId: CompanyId) {
    checkBackgroundThread()
    dao.erase(FavouriteCompanyMapper.map(companyId))
    firebaseAuth.uid?.let { userToken ->
      api.eraseBy(userToken, companyId.value)
    }
  }

  override suspend fun eraseAll(clearCloud: Boolean) {
    checkBackgroundThread()
    dao.eraseAll()
    if (clearCloud) {
      firebaseAuth.uid?.let { userToken ->
        api.eraseAll(userToken)
      }
    }
  }

  private suspend fun syncData(
    apiCompanies: List<CompanyId>,
    dbCompanies: List<CompanyId>
  ) {
    apiCompanies.itemsNotIn(dbCompanies).forEach {
      dao.insert(FavouriteCompanyMapper.map(it))
    }
    firebaseAuth.uid?.let { userToken ->
      dbCompanies.itemsNotIn(apiCompanies).forEach {
        api.putBy(userToken, it.value)
      }
    }
  }
}