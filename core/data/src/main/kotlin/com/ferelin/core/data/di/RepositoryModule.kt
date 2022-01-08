package com.ferelin.core.data.di

import com.ferelin.core.data.repository.*
import com.ferelin.core.domain.repository.*
import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModuleBinds::class])
class RepositoryModule

@Module
internal interface RepositoryModuleBinds {
  @Binds
  fun authRepository(
    authRepositoryImpl: AuthRepositoryImpl
  ): AuthRepository

  @Binds
  fun authUserStateRepository(
    authUserStateRepositoryImpl: AuthUserStateRepositoryImpl
  ): AuthUserStateRepository

  @Binds
  fun companyRepository(
    companyRepositoryImpl: CompanyRepositoryImpl
  ): CompanyRepository

  @Binds
  fun cryptoPriceRepository(
    cryptoPriceRepositoryImpl: CryptoPriceRepositoryImpl
  ): CryptoPriceRepository

  @Binds
  fun cryptoRepository(
    cryptoRepositoryImpl: CryptoRepositoryImpl
  ): CryptoRepository

  @Binds
  fun favouriteCompanyRepository(
    favouriteCompanyRepositoryImpl: FavouriteCompanyRepositoryImpl
  ): FavouriteCompanyRepository

  @Binds
  fun newsRepository(
    newsRepositoryImpl: NewsRepositoryImpl
  ): NewsRepository

  @Binds
  fun notifyPriceRepository(
    notifyPriceRepositoryImpl: NotifyPriceRepositoryImpl
  ): NotifyPriceRepository

  @Binds
  fun pastPriceRepository(
    pastPriceRepositoryImpl: PastPriceRepositoryImpl
  ): PastPriceRepository

  @Binds
  fun profileRepository(
    profileRepositoryImpl: ProfileRepositoryImpl
  ): ProfileRepository

  @Binds
  fun projectRepository(
    projectRepositoryImpl: ProjectRepositoryImpl
  ): ProjectRepository

  @Binds
  fun searchRequestsRepository(
    searchRequestsRepositoryImpl: SearchRequestsRepositoryImpl
  ): SearchRequestsRepository

  @Binds
  fun stockPriceRepository(
    stockPriceRepositoryImpl: StockPriceRepositoryImpl
  ): StockPriceRepository

  @Binds
  fun storagePathRepository(
    storagePathRepositoryImpl: StoragePathRepositoryImpl
  ): StoragePathRepository
}