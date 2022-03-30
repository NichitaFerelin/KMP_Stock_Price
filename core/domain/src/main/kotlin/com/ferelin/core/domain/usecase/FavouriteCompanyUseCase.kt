package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.FavouriteCompanyRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface FavouriteCompanyUseCase {
  val favouriteCompanies: Observable<List<CompanyId>>
  val favouriteCompaniesLce: Flow<LceState>
  fun addToFavourite(companyId: CompanyId)
  fun removeFromFavourite(companyId: CompanyId)
  fun eraseCache()
}

@Reusable
internal class FavouriteCompanyUseCaseImpl @Inject constructor(
  private val favouriteCompanyRepository: FavouriteCompanyRepository
) : FavouriteCompanyUseCase {
  override val favouriteCompanies: Observable<List<CompanyId>> = favouriteCompanyRepository.favouriteCompanies
    .doOnSubscribe { favouriteCompaniesLceState.value = LceState.Loading }
    .doOnEach { favouriteCompaniesLceState.value = LceState.Content }
    .doOnError { e -> favouriteCompaniesLceState.value = LceState.Error(e.message) }

  private val favouriteCompaniesLceState = MutableStateFlow<LceState>(LceState.None)
  override val favouriteCompaniesLce: Flow<LceState> = favouriteCompaniesLceState.asStateFlow()

  override fun addToFavourite(companyId: CompanyId) {
    favouriteCompanyRepository.addToFavourite(companyId)
  }

  override fun removeFromFavourite(companyId: CompanyId) {
    favouriteCompanyRepository.removeFromFavourite(companyId)
  }

  override fun eraseCache() {
    favouriteCompanyRepository.eraseAll(clearCloud = true)
  }
}