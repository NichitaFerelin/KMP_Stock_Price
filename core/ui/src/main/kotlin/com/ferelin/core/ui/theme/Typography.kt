package com.ferelin.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ferelin.core.ui.R

private val Eczar = FontFamily(
  Font(R.font.eczar_regular, FontWeight.Normal),
  Font(R.font.eczar_semibold, FontWeight.SemiBold),
)

@Immutable
data class AppTypography internal constructor(
  val title: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.SemiBold,
    fontSize = 34.sp,
    lineHeight = 41.sp,
  ),
  val largeTitle: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
  ),
  val subtitle: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 25.sp
  ),
  val bodyRegular: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 25.sp,
  ),
  val bodyMedium: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp,
    lineHeight = 22.sp,
  ),
  val body1: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 22.sp,
  ),
  val body2: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 20.sp,
  ),
  val bodySemibold: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
  ),
  val caption1: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 16.sp
  ),
  val caption2: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 13.sp,
  ),
  val button: TextStyle = TextStyle(
    fontFamily = Eczar,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    lineHeight = 18.sp,
  ),
)

internal val LocalAppTypography = staticCompositionLocalOf { AppTypography() }