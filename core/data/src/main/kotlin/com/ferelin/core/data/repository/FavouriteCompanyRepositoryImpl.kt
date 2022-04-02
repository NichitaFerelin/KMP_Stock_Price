package com.ferelin.core.data.repository

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

internal class FavouriteCompanyRepositoryImpl(
  private val dao: FavouriteCompanyDao,
  private val api: FavouriteCompanyApi,
  private val firebaseAuth: FirebaseAuth,
  externalScope: CoroutineScope,
  authUserStateRepository: AuthUserStateRepository
) : FavouriteCompanyRepository {

  init {
    authUserStateRepository.userToken
      .filterNot(String::isEmpty)
      .onEach(this::onTokenChanged)
      .launchIn(externalScope)
  }

  override val favouriteCompanies: Flow<List<CompanyId>>
    get() = dao.getAll().map { it.map(FavouriteCompanyMapper::map) }

  override suspend fun addToFavourite(companyId: CompanyId) {
    dao.insert(companyId.value)
    firebaseAuth.uid?.let { userToken ->
      api.putBy(userToken, companyId.value)
    }
  }

  override suspend fun removeFromFavourite(companyId: CompanyId) {
    dao.eraseBy(companyId.value)
    firebaseAuth.uid?.let { userToken ->
      api.eraseBy(userToken, companyId.value)
    }
  }

  override suspend fun eraseAll(clearCloud: Boolean) {
    dao.eraseAll()
    if (clearCloud) {
      firebaseAuth.uid?.let { userToken ->
        api.eraseAll(userToken)
      }
    }
  }

  private suspend fun onTokenChanged(token: String) {
    val apiResponse = api.load(token).firstOrNull() ?: return
    val dbFavouriteCompanies = dao.getAll().firstOrNull() ?: emptyList()
    dao.insertAll(
      favouriteCompanyIds = apiResponse.data.itemsNotIn(dbFavouriteCompanies)
    )
    dbFavouriteCompanies
      .itemsNotIn(apiResponse.data)
      .forEach { api.putBy(token, it) }
  }
}