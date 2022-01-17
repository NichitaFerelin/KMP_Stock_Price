package com.ferelin.features.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.component.APP_TOP_PADDING
import com.ferelin.core.ui.component.ClickableIcon
import com.ferelin.core.ui.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun LoginRoute(deps: LoginDeps) {
  val component = DaggerLoginComponent.builder()
    .dependencies(deps)
    .build()
  val viewModel = viewModel<LoginViewModel>(
    factory = component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  LoginScreen(
    uiState = uiState,
    onSendCodeClick = { },
    onPhoneChanged = viewModel::onPhoneChanged,
    onCodeChanged = viewModel::onCodeChanged
  )
}

@Composable
private fun LoginScreen(
  uiState: LoginStateUi,
  onSendCodeClick: () -> Unit,
  onPhoneChanged: (String) -> Unit,
  onCodeChanged: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    ClickableIcon(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(APP_TOP_PADDING),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      painter = painterResource(R.drawable.ic_arrow_back_24),
      contentDescription = stringResource(R.string.descriptionBack),
      tint = AppTheme.colors.buttonPrimary,
      onClick = { }
    )
    Column(
      modifier = Modifier.padding(
        top = APP_TOP_PADDING,
        start = 10.dp,
        end = 10.dp
      ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        painter = painterResource(R.mipmap.tesla),
        contentDescription = null
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.hintAuthentication),
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.hintAuthenticationHelp),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textTertiary,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.hintEnterPhone),
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = uiState.inputPhone,
        onValueChange = onPhoneChanged
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.hintEnterCode),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = uiState.inputCode,
        onValueChange = onCodeChanged
      )
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        modifier = Modifier.size(50.dp),
        onClick = onSendCodeClick
      ) {
        Text(text = "Send code temp")
      }
    }
  }
}