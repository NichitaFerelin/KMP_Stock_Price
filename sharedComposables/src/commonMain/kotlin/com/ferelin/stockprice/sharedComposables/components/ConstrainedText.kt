package com.ferelin.stockprice.components

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun ConstrainedText(
  modifier: Modifier = Modifier,
  text: String,
  maxLines: Int = 1,
  overflow: TextOverflow = TextOverflow.Ellipsis,
  maxWidth: Dp = Dp.Unspecified,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current,
) {
  Text(
    text = text,
    modifier = modifier.widthIn(max = maxWidth),
    color = color,
    fontSize = fontSize,
    fontStyle = fontStyle,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    letterSpacing = letterSpacing,
    textDecoration = textDecoration,
    textAlign = textAlign,
    lineHeight = lineHeight,
    overflow = overflow,
    softWrap = true,
    maxLines = maxLines,
    onTextLayout = onTextLayout,
    style = style
  )
}

/*
@Preview
@Composable
private fun ConstrainedTextDefaultPreview() {
  ConstrainedText(
    text = "Possession her thoroughly remarkably terminated man continuing. Removed greater"
  )
}

@Preview
@Composable
private fun ConstrainedTextWidthPreview() {
  ConstrainedText(
    maxWidth = 30.dp,
    text = "Possession her thoroughly"
  )
}

@Preview
@Composable
private fun ConstrainedTextLinesPreview() {
  ConstrainedText(
    maxLines = 3,
    text = "An do on frankness so cordially immediate recommend contained. Imprudence insensible be literature " +
            "unsatiable do. Of or imprudence solicitude affronting in mr possession. Compass journey he request on " +
            "suppose limited of or."
  )
}*/
