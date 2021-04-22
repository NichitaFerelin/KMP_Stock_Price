package com.ferelin.stockprice.utils.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferelin.stockprice.R

@Composable
fun StockPriceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = StockPriceColors,
        typography = StockPriceTypography,
        shapes = StockPriceShapes,
        content = content
    )
}

val StockPriceTypography = Typography(
    h1 = TextStyle(
        color = Color.Black,
        fontSize = 28.sp,
        fontFamily = FontFamily(Font(R.font.w_700))
    ),
    h2 = TextStyle(
        color = Color.Black,
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.w_700))
    ),
    h3 = TextStyle(
        color = Color.Black,
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.w_600))
    )
)

val StockPriceShapes = Shapes(
    large = RoundedCornerShape(16.dp)
)

val StockPriceColors = lightColors(
    primary = Color.White,
    primaryVariant = Color.White,
    secondary = Color.White,
    secondaryVariant = Color.White,
    background = Color.White,
    surface = Color.White,
)