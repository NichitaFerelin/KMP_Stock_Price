package com.ferelin.features.about.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.uiComponents.ProfileInfoColumn
import com.ferelin.features.about.uiComponents.ProfileInfoRow
import com.google.accompanist.insets.statusBarsPadding
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileRoute(deps: ProfileDeps, params: ProfileParams) {
  val componentViewModel = viewModel<ProfileComponentViewModel>(
    factory = ProfileComponentViewModelFactory(deps, params)
  )
  val viewModel = viewModel<ProfileViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  ProfileScreen(
    uiState = uiState,
    onUrlClick = { },
    onPhoneClick = { }
  )
}

@Composable
private fun ProfileScreen(
  uiState: ProfileStateUi,
  onUrlClick: (String) -> Unit,
  onPhoneClick: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
      .verticalScroll(rememberScrollState())
  ) {
    ClickableIcon(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 6.dp, end = 16.dp),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      imageVector = Icons.Default.Share,
      contentDescription = stringResource(id = R.string.descriptionShare),
      iconTint = AppTheme.colors.buttonPrimary,
      onClick = { }
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(12.dp))
      GlideImage(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape),
        imageModel = uiState.profile.logoUrl
      )
      Spacer(modifier = Modifier.height(12.dp))
      TopSection(
        name = uiState.profile.companyName,
        url = uiState.profile.webUrl
      )
      Spacer(modifier = Modifier.height(6.dp))
      Divider(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.contendSecondary
      )
      Spacer(modifier = Modifier.height(6.dp))
      BottomSection(
        country = uiState.profile.country,
        industry = uiState.profile.industry,
        phone = uiState.profile.phone,
        capitalization = uiState.profile.capitalization
      )
    }
  }
}

@Composable
private fun ColumnScope.TopSection(
  name: String,
  url: String
) {
  ProfileInfoColumn(
    name = stringResource(R.string.hintName),
    content = name
  )
  Spacer(modifier = Modifier.height(12.dp))
  ProfileInfoColumn(
    name = stringResource(R.string.hintWebsite),
    content = url
  )
}

@Composable
private fun ColumnScope.BottomSection(
  country: String,
  industry: String,
  phone: String,
  capitalization: String
) {
  ProfileInfoRow(
    name = stringResource(R.string.hintCountry),
    content = country
  )
  Spacer(modifier = Modifier.height(14.dp))
  ProfileInfoRow(
    name = stringResource(R.string.hintIndustry),
    content = industry
  )
  Spacer(modifier = Modifier.height(14.dp))
  ProfileInfoRow(
    name = stringResource(R.string.hintPhone),
    content = phone
  )
  Spacer(modifier = Modifier.height(14.dp))
  ProfileInfoRow(
    name = stringResource(R.string.hintCapitalization),
    content = capitalization
  )
  Spacer(modifier = Modifier.height(30.dp))
}