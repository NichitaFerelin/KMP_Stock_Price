@file:OptIn(ExperimentalMaterialApi::class)

package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.FailIcon
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun ExpandedStockItem(
    modifier: Modifier = Modifier,
    ticker: String,
    name: String,
    industry: String,
    logoUrl: String,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = APP_CONTENT_PADDING),
        shape = RoundedCornerShape(6.dp),
        elevation = 0.dp,
        backgroundColor = if (index % 2 == 0) {
            AppTheme.colors.contendPrimary
        } else AppTheme.colors.contendTertiary,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.Center,
            ) {
                ConstrainedText(
                    text = ticker,
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConstrainedText(
                    text = name,
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConstrainedText(
                    text = industry,
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textPrimary
                )
            }
            GlideImage(
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(6.dp)),
                imageModel = logoUrl,
                contentScale = ContentScale.Inside,
                failure = { FailIcon() }
            )
        }
    }
}