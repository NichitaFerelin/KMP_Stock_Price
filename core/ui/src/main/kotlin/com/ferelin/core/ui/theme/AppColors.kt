package com.ferelin.core.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Stable
class AppColors(
  statusBar: Color,
  contendAccentPrimary: Color,
  backgroundPrimary: Color,
  contendPrimary: Color,
  contendSecondary: Color,
  contendTertiary: Color,
  textPrimary: Color,
  textSecondary: Color,
  textTertiary: Color,
  indicatorContendError: Color,
  indicatorContendDone: Color,
  buttonPrimary: Color,
  buttonSecondary: Color,
  iconActive: Color,
  iconDisabled: Color,
  textPositive: Color,
  textNegative: Color,
  textClickable: Color,
  isDark: Boolean,
) {
  var statusBar by mutableStateOf(statusBar)
    private set
  var contendAccentPrimary by mutableStateOf(contendAccentPrimary)
    private set
  var backgroundPrimary by mutableStateOf(backgroundPrimary)
    private set
  var contendPrimary by mutableStateOf(contendPrimary)
    private set
  var contendSecondary by mutableStateOf(contendSecondary)
    private set
  var contendTertiary by mutableStateOf(contendTertiary)
    private set
  var textPrimary by mutableStateOf(textPrimary)
    private set
  var textSecondary by mutableStateOf(textSecondary)
    private set
  var textTertiary by mutableStateOf(textTertiary)
    private set
  var indicatorContendError by mutableStateOf(indicatorContendError)
    private set
  var indicatorContendDone by mutableStateOf(indicatorContendDone)
    private set
  var buttonPrimary by mutableStateOf(buttonPrimary)
    private set
  var buttonSecondary by mutableStateOf(buttonSecondary)
    private set
  var iconActive by mutableStateOf(iconActive)
    private set
  var iconDisabled by mutableStateOf(iconDisabled)
    private set
  var textPositive by mutableStateOf(textPositive)
    private set
  var textNegative by mutableStateOf(textNegative)
    private set
  var textClickable by mutableStateOf(textClickable)
    private set
  var isDark by mutableStateOf(isDark)
    private set

  fun update(other: AppColors) {
    backgroundPrimary = other.backgroundPrimary
    contendAccentPrimary = other.contendAccentPrimary
    contendPrimary = other.contendPrimary
    contendSecondary = other.contendSecondary
    contendTertiary = other.contendTertiary
    textPrimary = other.textPrimary
    textSecondary = other.textSecondary
    textTertiary = other.textTertiary
    indicatorContendError = other.indicatorContendError
    indicatorContendDone = other.indicatorContendDone
    buttonPrimary = other.buttonPrimary
    buttonSecondary = other.buttonSecondary
    iconActive = other.iconActive
    iconDisabled = other.iconDisabled
    textPositive = other.textPositive
    textNegative = other.textNegative
    textClickable = other.textClickable
    isDark = other.isDark
  }
}
