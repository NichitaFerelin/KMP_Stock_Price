package com.ferelin.features.about.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun NewsItem(
    modifier: Modifier = Modifier,
    source: String,
    url: String,
    date: String,
    title: String,
    content: String,
    onUrlClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = AppTheme.colors.backgroundPrimary
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            RowWithDateAndCategory(
                source = source,
                date = date
            )
            Spacer(modifier = Modifier.height(8.dp))
            ConstrainedText(
                text = title,
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            ConstrainedText(
                text = content,
                maxLines = 4,
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            RowOpenInBrowser(
                url = url,
                onUrlClick = onUrlClick
            )
        }
    }
}

@Composable
private fun RowWithDateAndCategory(
    modifier: Modifier = Modifier,
    source: String,
    date: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ConstrainedText(
            text = source,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textTertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        ConstrainedText(
            text = date,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textTertiary
        )
    }
}

@Composable
private fun RowOpenInBrowser(
    modifier: Modifier = Modifier,
    url: String,
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
                .clickable { onUrlClick() }
                .widthIn(max = 150.dp),
            text = url,
            style = AppTheme.typography.caption1,
            color = AppTheme.colors.textClickable
        )
    }
}