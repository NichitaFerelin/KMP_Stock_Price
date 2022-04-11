package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.params.AboutParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AboutStateUi internal constructor(
    val companyTicker: String,
    val companyName: String,
    val isFavourite: Boolean = false,
    val selectedScreenIndex: Int = SCREEN_PROFILE_INDEX
)

class AboutViewModel internal constructor(
    private val aboutParams: AboutParams,
    private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatchersProvider: DispatchersProvider
) {
    private val viewModelState = MutableStateFlow(
        value = AboutStateUi(
            companyTicker = aboutParams.companyTicker,
            companyName = aboutParams.companyName
        )
    )
    val uiState = viewModelState.asStateFlow()

    private val onFavouriteSwitchEvent = MutableSharedFlow<Unit>()
    private val isCompanyFavourite = favouriteCompanyUseCase.favouriteCompanies
        .map { companies -> companies.find { it.value == aboutParams.companyId } != null }

    init {
        isCompanyFavourite
            .onEach(this::onCompanyIsFavourite)
            .flowOn(dispatchersProvider.IO)
            .launchIn(viewModelScope)

        onFavouriteSwitchEvent
            .zip(
                other = isCompanyFavourite,
                transform = { _, isFavourite -> isFavourite }
            )
            .onEach(this::switchRequested)
            .flowOn(dispatchersProvider.IO)
            .launchIn(viewModelScope)
    }

    fun onScreenSelected(index: Int) {
        viewModelState.update { it.copy(selectedScreenIndex = index) }
    }

    fun switchFavourite() {
        viewModelScope.launch(dispatchersProvider.IO) {
            onFavouriteSwitchEvent.emit(Unit)
        }
    }

    private suspend fun switchRequested(isFavourite: Boolean) {
        val companyId = CompanyId(aboutParams.companyId)
        if (isFavourite) {
            favouriteCompanyUseCase.removeFromFavourite(companyId)
        } else {
            favouriteCompanyUseCase.addToFavourite(companyId)
        }
    }

    private fun onCompanyIsFavourite(isFavourite: Boolean) {
        viewModelState.update { it.copy(isFavourite = isFavourite) }
    }
}

const val ABOUT_TOTAL_SCREENS = 2
const val SCREEN_PROFILE_INDEX = 0
const val SCREEN_NEWS_INDEX = 1