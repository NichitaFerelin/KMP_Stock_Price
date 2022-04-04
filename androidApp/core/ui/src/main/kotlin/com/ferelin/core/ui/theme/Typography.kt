package com.ferelin.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ferelin.core.ui.R

private val Montserrat = FontFamily(
  Font(R.font.montserrat_bold, FontWeight.Bold),
  Font(R.font.montserrat_semibold, FontWeight.SemiBold),
)

@Immutable
data class AppTypography internal constructor(
  val title1: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 32.sp,
  ),
  val title2: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
  ),
  val body1: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
  ),
  val body2: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
  ),
  val caption1: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
  ),
  val button: TextStyle = TextStyle(
    fontFamily = Montserrat,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
  ),
)

internal val LocalAppTypography = staticCompositionLocalOf { AppTypography() }