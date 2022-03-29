package com.ferelin.core.data.di

import com.ferelin.core.data.repository.*
import com.ferelin.core.domain.repository.*
import dagger.Binds
import dagger.Module

@Module(includes = [RepositoryModuleBinds::class])
class RepositoryModule

@Suppress("unused")
@Module
internal interface RepositoryModuleBinds {
  @Binds
  fun authRepository(impl: AuthRepositoryImpl): AuthRepository

  @Binds
  fun authUserStateRepository(impl: AuthUserStateRepositoryImpl): AuthUserStateRepository

  @Binds
  fun companyRepository(impl: CompanyRepositoryImpl): CompanyRepository

  @Binds
  fun cryptoPriceRepository(impl: CryptoPriceRepositoryImpl): CryptoPriceRepository

  @Binds
  fun cryptoRepository(impl: CryptoRepositoryImpl): CryptoRepository

  @Binds
  fun favouriteCompanyRepository(impl: FavouriteCompanyRepositoryImpl): FavouriteCompanyRepository

  @Binds
  fun newsRepository(impl: NewsRepositoryImpl): NewsRepository

  @Binds
  fun notifyPriceRepository(impl: NotifyPriceRepositoryImpl): NotifyPriceRepository

  @Binds
  fun pastPriceRepository(impl: PastPriceRepositoryImpl): PastPriceRepository

  @Binds
  fun profileRepository(impl: ProfileRepositoryImpl): ProfileRepository

  @Binds
  fun projectRepository(impl: ProjectRepositoryImpl): ProjectRepository

  @Binds
  fun searchRequestsRepository(impl: SearchRequestsRepositoryImpl): SearchRequestsRepository

  @Binds
  fun stockPriceRepository(impl: StockPriceRepositoryImpl): StockPriceRepository

  @Binds
  fun storagePathRepository(impl: StoragePathRepositoryImpl): StoragePathRepository
}