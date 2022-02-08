@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)

package com.ferelin.features.authentication

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.APP_TOP_PADDING
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.TextField
import com.ferelin.core.ui.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.delay

@Composable
fun LoginRoute(
  deps: LoginDeps,
  onBackRoute: () -> Unit
) {
  val componentViewModel = viewModel<LoginComponentViewModel>(
    factory = LoginComponentViewModelFactory(deps)
  )
  val viewModel = viewModel<LoginViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  val context = LocalContext.current

  LaunchedEffect(uiState.verificationComplete) {
    if (uiState.verificationComplete) {
      delay(300)
      onBackRoute()
    }
  }

  LoginScreen(
    uiState = uiState,
    onBackClick = onBackRoute,
    onSendCodeClick = { viewModel.onSendCodeClick(context as AppCompatActivity) },
    onPhoneChanged = viewModel::onPhoneChanged,
    onCodeChanged = viewModel::onCodeChanged
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginScreen(
  uiState: LoginStateUi,
  onBackClick: () -> Unit,
  onSendCodeClick: () -> Unit,
  onPhoneChanged: (String) -> Unit,
  onCodeChanged: (String) -> Unit,
) {
  val keyboardController = LocalSoftwareKeyboardController.current

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
      imageVector = Icons.Default.ArrowBack,
      contentDescription = stringResource(R.string.descriptionBack),
      iconTint = AppTheme.colors.buttonPrimary,
      onClick = {
        keyboardController?.hide()
        onBackClick()
      }
    )
    Column(
      modifier = Modifier.padding(
        top = APP_TOP_PADDING + 16.dp,
        start = 10.dp,
        end = 10.dp
      ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        painter = painterResource(R.mipmap.tesla),
        contentDescription = stringResource(id = R.string.descriptionDecorative)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.hintAuthentication),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.hintAuthenticationHelp),
        style = AppTheme.typography.body2,
        color = AppTheme.colors.textTertiary,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(16.dp))
      PhoneInput(
        inputValue = uiState.inputPhone,
        inputEnabled = uiState.inputPhoneEnabled,
        sendCodeVisible = uiState.sendCodeEnabled,
        onValueChange = onPhoneChanged,
        onSendCodeClick = onSendCodeClick
      )
      Spacer(modifier = Modifier.height(16.dp))
      CodeInput(
        inputValue = uiState.inputCode,
        inputEnabled = uiState.inputCodeEnabled,
        inputVisible = uiState.inputCodeVisible,
        onValueChange = onCodeChanged
      )
      Spacer(modifier = Modifier.height(12.dp))
      InfoSection(
        networkError = uiState.networkError,
        emptyPhoneError = uiState.emptyPhoneError,
        tooManyRequestsError = uiState.tooManyRequestsError,
        undefinedError = uiState.undefinedError,
        loading = uiState.loading
      )
    }
  }
}

@Composable
private fun ColumnScope.PhoneInput(
  inputValue: String,
  inputEnabled: Boolean,
  sendCodeVisible: Boolean,
  keyboardController: SoftwareKeyboardController? = null,
  onValueChange: (String) -> Unit,
  onSendCodeClick: () -> Unit
) {
  val buttonAlpha by animateFloatAsState(if (inputEnabled) 1f else 0.5f)

  TextField(
    modifier = Modifier.padding(horizontal = 36.dp),
    inputValue = inputValue,
    backgroundColor = AppTheme.colors.backgroundSecondary,
    placeholder = stringResource(id = R.string.hintEnterPhone),
    isEnabled = inputEnabled,
    onValueChange = onValueChange,
    keyboardActions = KeyboardActions { keyboardController?.hide() },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    leadingIcon = {
      Text(
        text = "+",
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
    }
  )
  Spacer(modifier = Modifier.height(16.dp))
  AnimatedVisibility(
    visible = sendCodeVisible,
    enter = slideInVertically { -it / 3 } + scaleIn(),
    exit = slideOutVertically { -it / 3 } + scaleOut()
  ) {
    Button(
      onClick = onSendCodeClick,
      colors = ButtonDefaults.buttonColors(
        backgroundColor = AppTheme.colors.backgroundSecondary.copy(alpha = buttonAlpha)
      )
    ) {
      Text(
        text = stringResource(id = R.string.hintSendCode),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
    }
  }
}

@Composable
private fun CodeInput(
  inputValue: String,
  inputEnabled: Boolean,
  inputVisible: Boolean,
  keyboardController: SoftwareKeyboardController? = null,
  onValueChange: (String) -> Unit,
) {
  AnimatedVisibility(
    visible = inputVisible,
    enter = slideInVertically { -it / 2 } + scaleIn(),
    exit = slideOutVertically { -it / 2 } + scaleOut()
  ) {
    TextField(
      modifier = Modifier.width(200.dp),
      inputValue = inputValue,
      isEnabled = inputEnabled,
      backgroundColor = AppTheme.colors.backgroundSecondary,
      placeholder = stringResource(id = R.string.hintEnterCode),
      onValueChange = onValueChange,
      keyboardActions = KeyboardActions { keyboardController?.hide() },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
  }
}

@Composable
private fun InfoSection(
  networkError: Boolean,
  emptyPhoneError: Boolean,
  tooManyRequestsError: Boolean,
  undefinedError: Boolean,
  loading: Boolean,
) {
  if (loading) {
    CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
  } else {
    val errorText = when {
      networkError -> stringResource(id = R.string.messageNetworkNotAvailable)
      emptyPhoneError -> stringResource(id = R.string.errorEmptyPhone)
      tooManyRequestsError -> stringResource(id = R.string.errorTooManyRequests)
      undefinedError -> stringResource(id = R.string.errorUndefined)
      else -> {
        return
      }
    }

    Text(
      modifier = Modifier.padding(horizontal = 12.dp),
      text = errorText,
      textAlign = TextAlign.Center,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textNegative
    )
  }
}