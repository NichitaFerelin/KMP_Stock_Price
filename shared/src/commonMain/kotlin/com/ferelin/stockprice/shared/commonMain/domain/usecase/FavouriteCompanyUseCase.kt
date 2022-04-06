package com.ferelin.common.domain.usecase

import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.LceState
import com.ferelin.common.domain.repository.FavouriteCompanyRepository
import kotlinx.coroutines.flow.*

interface FavouriteCompanyUseCase {
  val favouriteCompanies: Flow<List<CompanyId>>
  val favouriteCompaniesLce: Flow<LceState>
  suspend fun addToFavourite(companyId: CompanyId)
  suspend fun removeFromFavourite(companyId: CompanyId)
  suspend fun eraseCache()
}

internal class FavouriteCompanyUseCaseImpl(
  private val favouriteCompanyRepository: FavouriteCompanyRepository
) : FavouriteCompanyUseCase {
  override val favouriteCompanies: Flow<List<CompanyId>> =
    favouriteCompanyRepository.favouriteCompanies
      .onStart { favouriteCompaniesLceState.value = LceState.Loading }
      .onEach { favouriteCompaniesLceState.value = LceState.Content }
      .catch { e -> favouriteCompaniesLceState.value = LceState.Error(e.message) }

  private val favouriteCompaniesLceState = MutableStateFlow<LceState>(LceState.None)
  override val favouriteCompaniesLce: Flow<LceState> = favouriteCompaniesLceState.asStateFlow()

  override suspend fun addToFavourite(companyId: CompanyId) {
    favouriteCompanyRepository.addToFavourite(companyId)
  }

  override suspend fun removeFromFavourite(companyId: CompanyId) {
    favouriteCompanyRepository.removeFromFavourite(companyId)
  }

  override suspend fun eraseCache() {
    favouriteCompanyRepository.eraseAll(clearCloud = true)
  }
}