@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)

package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun TopSearchFieldEditable(
  modifier: Modifier = Modifier,
  inputText: String,
  showCloseIcon: Boolean,
  onTextChanged: (String) -> Unit,
  onBackClick: () -> Unit
) {
  val keyboardController = LocalSoftwareKeyboardController.current

  SearchField(
    modifier = modifier,
    borderWidth = 2.dp,
    onClick = { /**/ }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.padding(start = 12.dp))
      ClickableIcon(
        imageVector = Icons.Default.ArrowBack,
        backgroundColor = AppTheme.colors.backgroundPrimary,
        contentDescription = "Clear search field",
        iconTint = AppTheme.colors.buttonPrimary,
        onClick = {
          keyboardController?.hide()
          onBackClick.invoke()
        })
      Spacer(modifier = Modifier.width(8.dp))
      TextField(inputValue = inputText,
        placeholder = "Enter search request",
        onValueChange = onTextChanged,
        keyboardActions = KeyboardActions { keyboardController?.hide() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        trailingIcon = {
          AnimatedVisibility(
            visible = showCloseIcon,
            enter = scaleIn(),
            exit = scaleOut()
          ) {
            ClickableIcon(backgroundColor = AppTheme.colors.backgroundPrimary,
              imageVector = Icons.Default.Close,
              iconTint = AppTheme.colors.buttonPrimary,
              contentDescription = "Clear search request",
              onClick = { onTextChanged("") })
          }
        })
    }
  }
}