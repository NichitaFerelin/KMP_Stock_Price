package com.ferelin.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  typography: AppTypography = AppTheme.typography,
  content: @Composable () -> Unit,
) {
  val colors = if (useDarkTheme) DarkColorPalette else LightColorPalette
  val colorPalette = remember { colors }
  colorPalette.update(colors)

  val sysUiController = rememberSystemUiController()
  SideEffect {
    sysUiController.setSystemBarsColor(
      color = Color.Transparent,
      darkIcons = !useDarkTheme,
    )
  }

  MaterialTheme(
    colors = debugColors(),
    typography = Typography(),
  ) {
    CompositionLocalProvider(
      LocalAppColors provides colorPalette,
      LocalAppTypography provides typography,
      LocalContentColor provides colors.textPrimary,
      LocalTextSelectionColors provides textSelectionColors(colors),
      LocalRippleTheme provides AppRippleTheme(colors),
      content = content,
    )
  }
}

private fun textSelectionColors(colors: AppColors): TextSelectionColors {
  return TextSelectionColors(
    handleColor = colors.contendAccentPrimary,
    backgroundColor = colors.contendAccentPrimary.copy(alpha = 0.4f)
  )
}

object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

  val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current
}

private val LocalAppColors = staticCompositionLocalOf<AppColors> {
  error("No LocalAppColors provided")
}

fun debugColors() = Colors(
  primary = DebugColor,
  primaryVariant = DebugColor,
  secondary = DebugColor,
  secondaryVariant = DebugColor,
  background = DebugColor,
  surface = DebugColor,
  error = DebugColor,
  onPrimary = DebugColor,
  onSecondary = DebugColor,
  onBackground = DebugColor,
  onSurface = DebugColor,
  onError = DebugColor,
  isLight = true
)

private val DebugColor = Color.Magenta
