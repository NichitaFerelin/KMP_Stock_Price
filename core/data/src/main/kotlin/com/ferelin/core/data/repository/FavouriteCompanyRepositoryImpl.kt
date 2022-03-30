package com.ferelin.core.data.repository

import com.ferelin.core.ExternalScope
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.mapper.FavouriteCompanyMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.repository.FavouriteCompanyRepository
import com.ferelin.core.itemsNotIn
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FavouriteCompanyRepositoryImpl @Inject constructor(
  private val dao: FavouriteCompanyDao,
  private val api: FavouriteCompanyApi,
  private val firebaseAuth: FirebaseAuth,
  @ExternalScope private val externalScope: CoroutineScope,
  authUserStateRepository: AuthUserStateRepository
) : FavouriteCompanyRepository {

  init {
    authUserStateRepository.userToken
      .filterNot(String::isEmpty)
      .onEach(this::onTokenChanged)
      .launchIn(externalScope)
  }

  override val favouriteCompanies: Observable<List<CompanyId>>
    get() = dao.getAll().map { it.map(FavouriteCompanyMapper::map) }

  override fun addToFavourite(companyId: CompanyId) {
    dao.insert(FavouriteCompanyMapper.map(companyId))
    firebaseAuth.uid?.let { userToken ->
      api.putBy(userToken, companyId.value)
    }
  }

  override fun removeFromFavourite(companyId: CompanyId) {
    dao.erase(FavouriteCompanyMapper.map(companyId))
    firebaseAuth.uid?.let { userToken ->
      api.eraseBy(userToken, companyId.value)
    }
  }

  override fun eraseAll(clearCloud: Boolean) {
    dao.eraseAll()
    if (clearCloud) {
      firebaseAuth.uid?.let { userToken ->
        api.eraseAll(userToken)
      }
    }
  }

  private fun onTokenChanged(token: String) {
    val apiResponse = api.load(token).blockingFirst() ?: return
    val apiFavouriteCompanies = FavouriteCompanyMapper.map(apiResponse)
    val dbFavouriteCompanies = dao.getAll().blockingFirst()
    dao.insertAll(
      companies = apiFavouriteCompanies.itemsNotIn(dbFavouriteCompanies)
    )
    dbFavouriteCompanies
      .itemsNotIn(apiFavouriteCompanies)
      .forEach { api.putBy(token, it.id) }
  }
}