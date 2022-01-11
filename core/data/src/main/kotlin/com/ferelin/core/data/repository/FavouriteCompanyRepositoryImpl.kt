package com.ferelin.core.data.repository

import com.ferelin.core.ExternalScope
import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.mapper.FavouriteCompanyMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.repository.FavouriteCompanyRepository
import com.ferelin.core.itemsNotIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class FavouriteCompanyRepositoryImpl @Inject constructor(
  private val dao: FavouriteCompanyDao,
  private val api: FavouriteCompanyApi,
  private val firebaseAuth: FirebaseAuth,
  @ExternalScope private val externalScope: CoroutineScope,
  authUserStateRepository: AuthUserStateRepository
) : FavouriteCompanyRepository {
  init {
    authUserStateRepository.userToken
      .filterNot { it.isEmpty() }
      .onEach(this::onTokenChanged)
      .launchIn(externalScope)
  }

  override val favouriteCompanies: Flow<List<CompanyId>>
    get() = dao.getAll().map { it.map(FavouriteCompanyMapper::map) }

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

  private suspend fun onTokenChanged(token: String) {
    val apiResponse = api.load(token).firstOrNull() ?: return
    val apiFavouriteCompanies = FavouriteCompanyMapper.map(apiResponse)
    val dbFavouriteCompanies = dao.getAll().firstOrNull() ?: emptyList()
    dao.insertAll(
      companies = apiFavouriteCompanies.itemsNotIn(dbFavouriteCompanies)
    )
    dbFavouriteCompanies.itemsNotIn(apiFavouriteCompanies)
      .forEach { api.putBy(token, it.id) }
  }
}