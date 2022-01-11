package com.ferelin.stockprice.di

import android.content.Context
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
import com.ferelin.core.ui.di.RouterModule
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.features.about.ui.about.AboutDeps
import com.ferelin.features.about.ui.chart.ChartDeps
import com.ferelin.features.about.ui.news.NewsDeps
import com.ferelin.features.about.ui.profile.ProfileDeps
import com.ferelin.features.authentication.ui.LoginDeps
import com.ferelin.features.search.ui.SearchDeps
import com.ferelin.features.settings.ui.SettingsDeps
import com.ferelin.features.splash.ui.LoadingDeps
import com.ferelin.features.stocks.ui.common.CommonDeps
import com.ferelin.features.stocks.ui.defaults.StocksDeps
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksDeps
import com.ferelin.navigation.CoordinatorModule
import com.ferelin.stockprice.MainActivity
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
    RouterModule::class,
    CoroutineModule::class,
    NetworkListenerModule::class,
    CoordinatorModule::class,
    RouterHostModule::class,
    DownloadManagerModule::class
  ]
)
interface AppComponent :
  SearchDeps,
  AboutDeps,
  ChartDeps,
  NewsDeps,
  ProfileDeps,
  LoginDeps,
  SettingsDeps,
  LoadingDeps,
  CommonDeps,
  StocksDeps,
  FavouriteStocksDeps {

  override val context: Context
  override val coordinator: Coordinator
  override val searchRequestsUseCase: SearchRequestsUseCase
  override val favouriteCompanyUseCase: FavouriteCompanyUseCase
  override val companyUseCase: CompanyUseCase
  override val networkListener: NetworkListener
  override val pastPricesUseCase: PastPricesUseCase
  override val stockPricesUseCase: StockPriceUseCase
  override val newsUseCase: NewsUseCase
  override val profileUseCase: ProfileUseCase
  override val authUseCase: AuthUseCase
  override val permissionManager: PermissionManager
  override val storageManager: AppStorageManager
  override val authUserStateRepository: AuthUserStateRepository
  override val notifyPriceUseCase: NotifyPriceUseCase
  override val storagePathUseCase: StoragePathUseCase
  override val downloadProjectUseCase: DownloadProjectUseCase
  override val cryptoPriceUseCase: CryptoPriceUseCase
  override val cryptoUseCase: CryptoUseCase
  override val stockStyleProvider: StockStyleProvider

  fun inject(mainActivity: MainActivity)

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun context(context: Context): Builder

    fun build(): AppComponent
  }
}