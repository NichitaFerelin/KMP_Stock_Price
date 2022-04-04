package com.ferelin.features.settings

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.settings.uiComponents.SettingsDivider
import com.ferelin.features.settings.uiComponents.SettingsItem
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsRoute(
  onLogInRoute: () -> Unit,
  onBackRoute: () -> Unit
) {
  val viewModel = getViewModel<SettingsViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  val permissionsLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = viewModel::onPermissions
  )
  val pathLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri: Uri? ->
    viewModel.onStoragePathSelected(
      path = uri?.path ?: "",
      authority = uri?.authority ?: ""
    )
  }

  LaunchedEffect(key1 = uiState.requestPermissions) {
    if (uiState.requestPermissions) {
      permissionsLauncher.launch(WRITE_EXTERNAL_STORAGE)
    }
  }
  LaunchedEffect(key1 = uiState.requestStoragePath) {
    if (uiState.requestStoragePath) {
      pathLauncher.launch(Uri.EMPTY)
    }
  }

  SettingsScreen(
    uiState = uiState,
    onBackClick = onBackRoute,
    onLogInClick = onLogInRoute,
    onLogOutClick = viewModel::onLogOutClick,
    onClearDataClick = viewModel::onClearDataClick,
    onDownloadCodeClick = viewModel::onDownloadCodeClick,
  )
}

@Composable
private fun SettingsScreen(
  uiState: SettingsStateUi,
  onBackClick: () -> Unit,
  onLogInClick: () -> Unit,
  onLogOutClick: () -> Unit,
  onClearDataClick: () -> Unit,
  onDownloadCodeClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
  ) {
    ClickableIcon(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(12.dp),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      iconTint = AppTheme.colors.buttonPrimary,
      imageVector = Icons.Default.ArrowBack,
      contentDescription = "",
      onClick = onBackClick
    )
    Column(
      modifier = Modifier.padding(top = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.titleSettings),
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(20.dp))
      SettingsItem(
        title = stringResource(R.string.titleDownload),
        text = stringResource(R.string.sourceDownload),
        painter = painterResource(R.drawable.ic_download_30),
        onClick = onDownloadCodeClick
      )
      Spacer(modifier = Modifier.height(8.dp))
      SettingsDivider()
      Spacer(modifier = Modifier.height(8.dp))
      SettingsItem(
        title = stringResource(R.string.titleClearData),
        text = stringResource(R.string.sourceClearData),
        painter = painterResource(R.drawable.ic_delete_30),
        onClick = onClearDataClick
      )
      Spacer(modifier = Modifier.height(8.dp))
      SettingsDivider()
      Spacer(modifier = Modifier.height(8.dp))

      val isAuthenticated = uiState.isUserAuthenticated
      val context = LocalContext.current
      SettingsItem(
        title = stringResource(
          id = if (isAuthenticated) {
            R.string.titleAuthorized
          } else R.string.titleAuthorization
        ),
        text = stringResource(
          id = if (isAuthenticated) {
            R.string.sourceAuthorized
          } else R.string.sourceNotAuthorized
        ),
        painter = painterResource(
          if (isAuthenticated) {
            R.drawable.ic_logout_30
          } else R.drawable.ic_login_30
        ),
        onClick = if (isAuthenticated) onLogOutClick else onLogInClick
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}