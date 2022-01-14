package com.ferelin.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.settings.ui.component.SettingsDivider
import com.ferelin.features.settings.ui.component.SettingsItem
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SettingsRoute(settingsDeps: SettingsDeps) {
  val component = DaggerSettingsComponent.builder()
    .dependencies(settingsDeps)
    .build()

  val viewModel: SettingsViewModel by viewModel(
    factory = component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  SettingsScreen(
    settingsStateUi = uiState,
    onLogOutClick = viewModel::onLogOutClick,
    onClearDataClick = viewModel::onClearDataClick,
    onDownloadCodeClick = viewModel::onDownloadCodeClick,
    onPermissionsGranted = viewModel::onPermissionsGranted
  )
}

@Composable
internal fun SettingsScreen(
  settingsStateUi: SettingsStateUi,
  onLogOutClick: () -> Unit,
  onClearDataClick: () -> Unit,
  onDownloadCodeClick: () -> Unit,
  onPermissionsGranted: () -> Unit
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
  ) {
    Icon(
      painter = painterResource(R.drawable.ic_arrow_back_24),
      contentDescription = null
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = stringResource(R.string.titleSettings))
      Spacer(modifier = Modifier.height(8.dp))
      SettingsDivider()
      Spacer(modifier = Modifier.height(8.dp))
      SettingsItem(
        title = stringResource(R.string.titleDownload),
        subtitle = stringResource(R.string.sourceDownload),
        painter = painterResource(R.drawable.ic_download_30),
        onClick = onDownloadCodeClick
      )
      Spacer(modifier = Modifier.height(8.dp))
      SettingsItem(
        title = stringResource(R.string.titleClearData),
        subtitle = stringResource(R.string.sourceClearData),
        painter = painterResource(R.drawable.ic_delete_30),
        onClick = onClearDataClick
      )
      Spacer(modifier = Modifier.height(8.dp))
      val isAuthenticated = settingsStateUi.isUserAuthenticated
      SettingsItem(
        title = stringResource(
          id = if (isAuthenticated) {
            R.string.titleAuthorized
          } else R.string.titleAuthorization
        ),
        subtitle = stringResource(
          id = if (isAuthenticated) {
            R.string.sourceAuthorized
          } else R.string.sourceNotAuthorized
        ),
        painter = painterResource(
          if (isAuthenticated) {
            R.drawable.ic_logout_30
          } else R.drawable.ic_login_30
        ),
        onClick = onLogOutClick
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}