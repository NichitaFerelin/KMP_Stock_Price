package com.ferelin.core.domain.di

import com.ferelin.core.domain.usecase.*
import dagger.Binds
import dagger.Module

@Module(includes = [UseCaseModuleBinds::class])
class UseCaseModule

@Module
internal interface UseCaseModuleBinds {
  @Binds
  fun authUseCase(impl: AuthUseCaseImpl): AuthUseCase

  @Binds
  fun companyUseCase(impl: CompanyUseCaseImpl): CompanyUseCase

  @Binds
  fun cryptoPriceUseCase(impl: CryptoPriceUseCaseImpl): CryptoPriceUseCase

  @Binds
  fun cryptoUseCase(impl: CryptoUseCaseImpl): CryptoUseCase

  @Binds
  fun downloadProjectUseCase(impl: DownloadProjectUseCaseImpl): DownloadProjectUseCase

  @Binds
  fun favouriteCompanyUseCase(impl: FavouriteCompanyUseCaseImpl): FavouriteCompanyUseCase

  @Binds
  fun newsUseCase(impl: NewsUseCaseImpl): NewsUseCase

  @Binds
  fun notifyPriceUseCase(impl: NotifyPriceUseCaseImpl): NotifyPriceUseCase

  @Binds
  fun pastPricesUseCase(impl: PastPricesUseCaseImpl): PastPricesUseCase

  @Binds
  fun profileUseCase(impl: ProfileUseCaseImpl): ProfileUseCase

  @Binds
  fun searchRequestsUseCase(impl: SearchRequestsUseCaseImpl): SearchRequestsUseCase

  @Binds
  fun stockPriceUseCase(impl: StockPriceUseCaseImpl): StockPriceUseCase

  @Binds
  fun storagePathUseCase(impl: StoragePathUseCaseImpl): StoragePathUseCase
}