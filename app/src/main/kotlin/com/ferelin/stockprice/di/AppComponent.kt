package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.data.di.DownloadManagerModule
import com.ferelin.core.data.di.NetworkModule
import com.ferelin.core.data.di.RepositoryModule
import com.ferelin.core.data.di.StorageModule
import com.ferelin.core.di.CoroutineModule
import com.ferelin.core.di.NetworkListenerModule
import com.ferelin.core.domain.di.UseCaseModule
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.*
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import com.ferelin.features.about.about.AboutDeps
import com.ferelin.features.authentication.LoginDeps
import com.ferelin.features.search.SearchDeps
import com.ferelin.features.settings.SettingsDeps
import com.ferelin.features.stocks.overview.OverviewDeps
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    NetworkModule::class,
    RepositoryModule::class,
    StorageModule::class,
    UseCaseModule::class,
    CoroutineModule::class,
    NetworkListenerModule::class,
    DownloadManagerModule::class
  ]
)
interface AppComponent :
  AboutDeps,
  LoginDeps,
  SearchDeps,
  SettingsDeps,
  OverviewDeps {

  override val favouriteCompanyUseCase: FavouriteCompanyUseCase
  override val dispatchersProvider: DispatchersProvider
  override val networkListener: NetworkListener
  override val pastPricesUseCase: PastPricesUseCase
  override val stockPricesUseCase: StockPriceUseCase
  override val newsUseCase: NewsUseCase
  override val profileUseCase: ProfileUseCase
  override val companyUseCase: CompanyUseCase
  override val authUseCase: AuthUseCase
  override val searchRequestsUseCase: SearchRequestsUseCase
  override val permissionManager: PermissionManager
  override val storageManager: AppStorageManager
  override val authUserStateRepository: AuthUserStateRepository
  override val notifyPriceUseCase: NotifyPriceUseCase
  override val storagePathUseCase: StoragePathUseCase
  override val downloadProjectUseCase: DownloadProjectUseCase
  override val cryptoPriceUseCase: CryptoPriceUseCase
  override val cryptoUseCase: CryptoUseCase

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun context(context: Context): Builder

    fun build(): AppComponent
  }
}