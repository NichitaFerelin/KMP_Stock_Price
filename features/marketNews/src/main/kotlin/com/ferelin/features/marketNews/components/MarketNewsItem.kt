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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun MarketNewsItem(
    modifier: Modifier = Modifier,
    headline: String,
    summary: String,
    sourceUrl: String,
    imageUrl: String,
    category: String,
    date: String
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
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                imageModel = imageUrl
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            .clickable { /**/ }
                            .widthIn(max = 150.dp),
                        text = sourceUrl,
                        style = AppTheme.typography.caption1,
                        color = AppTheme.colors.textClickable
                    )
                }
            }
        }
    }
}