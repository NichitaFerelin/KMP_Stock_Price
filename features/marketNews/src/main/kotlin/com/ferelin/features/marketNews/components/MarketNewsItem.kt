package com.ferelin.features.marketNews.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.GlideIcon
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun MarketNewsItem(
    modifier: Modifier = Modifier,
    headline: String,
    summary: String,
    sourceUrl: String,
    imageUrl: String,
    category: String,
    date: String,
    onUrlClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = AppTheme.colors.backgroundPrimary,
        elevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = AppTheme.colors.buttonSecondary
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GlideIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                imageModel = imageUrl,
                contentScale = ContentScale.None
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                RowWithDateAndCategory(
                    category = category,
                    date = date
                )
                Spacer(modifier = Modifier.height(12.dp))
                ConstrainedText(
                    text = headline,
                    style = AppTheme.typography.title2,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConstrainedText(
                    text = summary,
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(12.dp))
                RowOpenInBrowser(
                    sourceUrl = sourceUrl,
                    onUrlClick = onUrlClick
                )
            }
        }
    }
}

@Composable
private fun RowWithDateAndCategory(
    modifier: Modifier = Modifier,
    category: String,
    date: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ConstrainedText(
            text = category,
            style = AppTheme.typography.caption1,
            color = AppTheme.colors.textTertiary
        )
        ConstrainedText(
            text = date,
            style = AppTheme.typography.caption1,
            color = AppTheme.colors.textClickable
        )
    }
}

@Composable
private fun RowOpenInBrowser(
    modifier: Modifier = Modifier,
    sourceUrl: String,
    onUrlClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.hintOpenInBrowser),
            style = AppTheme.typography.caption1,
            color = AppTheme.colors.textTertiary
        )
        ConstrainedText(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onUrlClick)
                .widthIn(max = 150.dp),
            text = sourceUrl,
            style = AppTheme.typography.caption1,
            color = AppTheme.colors.textClickable
        )
    }
}