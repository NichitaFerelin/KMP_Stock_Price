package com.ferelin.core.domain.di

import com.ferelin.core.domain.usecase.*
import dagger.Binds
import dagger.Module

@Module(includes = [UseCaseModuleBinds::class])
class UseCaseModule

@Module
internal interface UseCaseModuleBinds {
  @Binds
  fun authUseCase(
    authUseCaseImpl: AuthUseCaseImpl
  ): AuthUseCase

  @Binds
  fun companyUseCase(
    companyUseCaseImpl: CompanyUseCaseImpl
  ): CompanyUseCase

  @Binds
  fun cryptoPriceUseCase(
    cryptoPriceUseCaseImpl: CryptoPriceUseCaseImpl
  ): CryptoPriceUseCase

  @Binds
  fun cryptoUseCase(
    cryptoUseCaseImpl: CryptoUseCaseImpl
  ): CryptoUseCase

  @Binds
  fun downloadProjectUseCase(
    downloadProjectUseCaseImpl: DownloadProjectUseCaseImpl
  ): DownloadProjectUseCase

  @Binds
  fun favouriteCompanyUseCase(
    favouriteCompanyUseCaseImpl: FavouriteCompanyUseCaseImpl
  ): FavouriteCompanyUseCase

  @Binds
  fun newsUseCase(
    newsUseCaseImpl: NewsUseCaseImpl
  ): NewsUseCase

  @Binds
  fun notifyPriceUseCase(
    notifyPriceUseCaseImpl: NotifyPriceUseCaseImpl
  ): NotifyPriceUseCase

  @Binds
  fun pastPricesUseCase(
    pastPricesUseCaseImpl: PastPricesUseCaseImpl
  ): PastPricesUseCase

  @Binds
  fun profileUseCase(
    profileUseCaseImpl: ProfileUseCaseImpl
  ): ProfileUseCase

  @Binds
  fun searchRequestsUseCase(
    searchRequestsUseCaseImpl: SearchRequestsUseCaseImpl
  ): SearchRequestsUseCase

  @Binds
  fun stockPriceUseCase(
    stockPriceUseCaseImpl: StockPriceUseCaseImpl
  ): StockPriceUseCase

  @Binds
  fun storagePathUseCase(
    storagePathUseCaseImpl: StoragePathUseCaseImpl
  ): StoragePathUseCase
}