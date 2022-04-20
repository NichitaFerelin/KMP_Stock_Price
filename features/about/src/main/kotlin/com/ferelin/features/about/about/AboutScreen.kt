package com.ferelin.features.about.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.APP_TOOLBAR_BASELINE
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.GlideIcon
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.utils.openUrl
import com.ferelin.features.about.components.ProfileRow
import com.ferelin.features.about.components.ProfitValue
import com.ferelin.features.about.news.NewsScreenRoute
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AboutScreenRoute(
    companyId: Int,
    onBackRoute: () -> Unit
) {
    val viewModel = getViewModel<AboutViewModel>(
        parameters = { parametersOf(companyId) }
    )
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    AboutScreen(
        uiState = uiState,
        onBackClick = onBackRoute,
        onFavoriteIconClick = viewModel::switchFavorite,
        onUrlClick = { context.openUrl(uiState.companyProfile.webUrl) },
        onNewsRoute = { NewsScreenRoute(companyId = companyId) }
    )
}

@Composable
private fun AboutScreen(
    uiState: AboutUiState,
    onBackClick: () -> Unit,
    onFavoriteIconClick: () -> Unit,
    onUrlClick: () -> Unit,
    onNewsRoute: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.backgroundPrimary,
                        AppTheme.colors.backgroundSecondary
                    ),
                    startY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE / 3).toPx() },
                    endY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE * 2).toPx() }
                )
            )
    ) {
        TopBarWithProfile(
            companyProfile = uiState.companyProfile,
            companiesLce = uiState.companyProfileLce,
            stockPriceViewData = uiState.stockPrice,
            stockPriceLceState = uiState.stockPriceLce,
            stockPriceFetchLceState = uiState.stockPriceFetchLce,
            onBackClick = onBackClick,
            onFavoriteIconClick = onFavoriteIconClick,
            onUrlClick = onUrlClick
        )
        BodyContent(onNewsRoute = onNewsRoute)
    }
}

@Composable
private fun TopBarWithProfile(
    companyProfile: CompanyProfileViewData,
    companiesLce: LceState,
    stockPriceViewData: StockPriceViewData,
    stockPriceLceState: LceState,
    stockPriceFetchLceState: LceState,
    onBackClick: () -> Unit,
    onFavoriteIconClick: () -> Unit,
    onUrlClick: () -> Unit
) {
    TopRowWithCompanyIcon(
        modifier = Modifier.padding(
            top = 6.dp,
            start = 6.dp,
            end = 6.dp
        ),
        companiesLce = companiesLce,
        companyProfile = companyProfile,
        onFavoriteIconClick = onFavoriteIconClick,
        onBackClick = onBackClick
    )
    Spacer(modifier = Modifier.height(14.dp))
    ProfitValue(
        value = stockPriceViewData.price,
        lceState = stockPriceLceState,
        fetchLceState = stockPriceFetchLceState
    )
    Spacer(modifier = Modifier.height(12.dp))
    CompanyProfile(
        modifier = Modifier.padding(horizontal = APP_CONTENT_PADDING),
        companyProfile = companyProfile,
        companiesLce = companiesLce,
        onUrlClick = onUrlClick
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun BodyContent(
    onNewsRoute: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(AppTheme.colors.backgroundPrimary)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.titleNews),
            style = AppTheme.typography.title2,
            color = AppTheme.colors.textPrimary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(AppTheme.colors.contendAccentPrimary)
        )
    }

    onNewsRoute()
}

@Composable
private fun TopRowWithCompanyIcon(
    modifier: Modifier = Modifier,
    companiesLce: LceState,
    companyProfile: CompanyProfileViewData,
    onFavoriteIconClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ClickableIcon(
            painter = painterResource(id = R.drawable.ic_back_24),
            contentDescription = stringResource(id = R.string.descriptionBack),
            tint = AppTheme.colors.buttonPrimary,
            onClick = onBackClick
        )

        when (companiesLce) {
            is LceState.Loading -> {
                Box(
                    modifier = Modifier.size(PROFILE_IMAGE_SIZE),
                    contentAlignment = Alignment.Center
                ) {
                    AppCircularProgressIndicator()
                }
            }
            is LceState.Content -> {
                GlideIcon(
                    modifier = Modifier
                        .size(PROFILE_IMAGE_SIZE)
                        .clip(RoundedCornerShape(6.dp)),
                    imageModel = companyProfile.logoUrl
                )
            }
            else -> Unit
        }

        ClickableIcon(
            painter = painterResource(id = R.drawable.ic_favorite_16),
            contentDescription = stringResource(id = R.string.descriptionFavoriteStock),
            tint = if (companyProfile.isFavorite) {
                AppTheme.colors.iconActive
            } else AppTheme.colors.iconDisabled,
            onClick = onFavoriteIconClick
        )
    }
}

@Composable
private fun CompanyProfile(
    modifier: Modifier = Modifier,
    companyProfile: CompanyProfileViewData,
    companiesLce: LceState,
    onUrlClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (companiesLce) {
            is LceState.Loading -> {
                AppCircularProgressIndicator()
            }
            is LceState.Content -> {
                if (companyProfile.phone.isNotEmpty()) {
                    ProfileRow(
                        title = stringResource(id = R.string.hintPhone),
                        value = companyProfile.phone
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (companyProfile.webUrl.isNotEmpty()) {
                    ProfileRow(
                        title = stringResource(id = R.string.hintWebsite),
                        value = companyProfile.webUrl,
                        onClick = onUrlClick
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (companyProfile.capitalization.isNotEmpty()) {
                    ProfileRow(
                        title = stringResource(id = R.string.hintCapitalization),
                        value = companyProfile.capitalization
                    )
                }
            }
            else -> Unit
        }
    }
}

private val PROFILE_IMAGE_SIZE = 80.dp