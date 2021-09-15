/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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