package com.ferelin.features.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding

@Composable
internal fun LoginRoute(loginViewModel: LoginViewModel) {
  val uiState by loginViewModel.uiState.collectAsState()

  LoginScreen(
    loginStateUi = uiState,
    onSendCodeClick = { },
    onPhoneChanged = loginViewModel::onPhoneChanged,
    onCodeChanged = loginViewModel::onCodeChanged
  )
}

@Composable
internal fun LoginScreen(
  loginStateUi: LoginStateUi,
  onSendCodeClick: () -> Unit,
  onPhoneChanged: (String) -> Unit,
  onCodeChanged: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .padding(top = 8.dp)
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Icon(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 8.dp),
      painter = painterResource(R.drawable.ic_arrow_back_24),
      contentDescription = stringResource(R.string.descriptionBack)
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row {
        Image(
          painter = painterResource(R.mipmap.facebook),
          contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
          painter = painterResource(R.mipmap.facebook),
          contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
          painter = painterResource(R.mipmap.facebook),
          contentDescription = null
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(text = stringResource(R.string.hintAuthentication))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = stringResource(R.string.hintAuthenticationHelp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = stringResource(R.string.hintEnterPhone))
      Spacer(modifier = Modifier.height(4.dp))
      TextField(
        value = loginStateUi.inputPhone,
        onValueChange = onPhoneChanged
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = stringResource(R.string.hintEnterCode))
      Spacer(modifier = Modifier.height(4.dp))
      TextField(
        value = loginStateUi.inputCode,
        onValueChange = onCodeChanged
      )
      Spacer(modifier = Modifier.height(4.dp))
      Button(
        modifier = Modifier.size(50.dp),
        onClick = onSendCodeClick
      ) {
        Text(text = "Send code temp")
      }
    }
  }
}