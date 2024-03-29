package com.ferelin.stockprice.sharedComposables.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.ferelin.stockprice.sharedComposables.NativeThemeSettings

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    typography: AppTypography = AppTheme.typography,
    content: @Composable () -> Unit,
) {
    val colors = if (useDarkTheme) DarkColorPalette else LightColorPalette
    val colorPalette = remember { colors }
    colorPalette.update(colors)

    val selectionColors = remember {
        TextSelectionColors(
            handleColor = colors.textPrimary,
            backgroundColor = colors.contendAccentPrimary
        )
    }

    NativeThemeSettings(useDarkTheme, colors)

    MaterialTheme(
        colors = debugColors(),
        typography = Typography(),
    ) {
        CompositionLocalProvider(
            LocalAppColors provides colorPalette,
            LocalAppTypography provides typography,
            LocalRippleTheme provides AppRippleTheme(colors),
            LocalTextSelectionColors provides selectionColors,
            content = content,
        )
    }
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
