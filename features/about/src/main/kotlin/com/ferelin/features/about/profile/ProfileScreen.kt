package com.ferelin.features.about.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.ui.component.ProfileInfoRow
import com.google.accompanist.insets.statusBarsPadding

@Composable
internal fun ProfileRoute(profileViewModel: ProfileViewModel) {
  val uiState by profileViewModel.uiState.collectAsState()

  ProfileScreen(
    profileStateUi = uiState,
    onUrlClick = { },
    onPhoneClick = { }
  )
}

@Composable
internal fun ProfileScreen(
  profileStateUi: ProfileStateUi,
  onUrlClick: (String) -> Unit,
  onPhoneClick: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    /*
    * Glide load im
    * */
    Text(text = stringResource(R.string.hintName))
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = profileStateUi.profile.companyName)
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = stringResource(R.string.hintWebsite))
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = profileStateUi.profile.webUrl)
    Spacer(modifier = Modifier.height(4.dp))
    Divider(
      modifier = Modifier.fillMaxWidth(),
      color = AppTheme.colors.backgroundSecondary
    )
    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
      ProfileInfoRow(
        name = stringResource(R.string.hintCountry),
        content = profileStateUi.profile.country
      )
      Spacer(modifier = Modifier.height(4.dp))
      ProfileInfoRow(
        name = stringResource(R.string.hintIndustry),
        content = profileStateUi.profile.industry
      )
      Spacer(modifier = Modifier.height(4.dp))
      ProfileInfoRow(
        name = stringResource(R.string.hintPhone),
        content = profileStateUi.profile.phone
      )
      Spacer(modifier = Modifier.height(4.dp))
      ProfileInfoRow(
        name = stringResource(R.string.hintCapitalization),
        content = profileStateUi.profile.capitalization
      )
    }
  }
}