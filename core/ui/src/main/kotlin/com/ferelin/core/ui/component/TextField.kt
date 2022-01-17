package com.ferelin.core.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun TextField(
  modifier: Modifier = Modifier,
  inputValue: String,
  placeholder: String,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  isEnabled: Boolean = true,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions(),
  onValueChange: (String) -> Unit,
) {
  androidx.compose.material.TextField(
    modifier = modifier
      .heightIn(min = 52.dp)
      .fillMaxWidth(),
    value = inputValue,
    onValueChange = onValueChange,
    textStyle = AppTheme.typography.body1,
    label = null,
    trailingIcon = trailingIcon,
    isError = isError,
    enabled = isEnabled,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = true,
    maxLines = 1,
    leadingIcon = {
      Icon(
        painter = painterResource(id = R.drawable.ic_close_24),
        contentDescription = "",
        tint = AppTheme.colors.backgroundPrimary
      )
    },
    placeholder = {
      Text(
        text = placeholder,
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
    },
    shape = RoundedCornerShape(26.dp),
    colors = TextFieldDefaults.textFieldColors(
      backgroundColor = AppTheme.colors.backgroundPrimary,
      textColor = AppTheme.colors.textPrimary,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      errorIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
      cursorColor = AppTheme.colors.contendAccentPrimary,
      placeholderColor = AppTheme.colors.contendAccentPrimary,
      disabledPlaceholderColor = AppTheme.colors.contendAccentPrimary,
      disabledTextColor = Color.Transparent,
      errorCursorColor = Color.Transparent,
      leadingIconColor = Color.Transparent,
      disabledLabelColor = Color.Transparent,
      disabledLeadingIconColor = Color.Transparent,
      disabledTrailingIconColor = Color.Transparent,
      errorLabelColor = Color.Transparent,
      errorLeadingIconColor = Color.Transparent,
      errorTrailingIconColor = Color.Transparent,
      trailingIconColor = Color.Transparent,
      focusedLabelColor = AppTheme.colors.contendAccentPrimary,
      unfocusedLabelColor = AppTheme.colors.contendAccentPrimary
    )
  )
}