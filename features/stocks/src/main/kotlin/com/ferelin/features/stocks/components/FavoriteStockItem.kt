@file:OptIn(ExperimentalMaterialApi::class)

package com.ferelin.features.stocks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun FavoriteStockItem(
    modifier: Modifier = Modifier,
    name: String,
    logoUrl: String,
    industry: String,
    onClick: () -> Unit,
    onFavoriteIconClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .height(90.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary,
        elevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = AppTheme.colors.buttonSecondary
        ),
        shape = RoundedCornerShape(6.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    imageModel = logoUrl
                )
                Spacer(modifier = Modifier.height(8.dp))
                ConstrainedText(
                    text = name,
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                ConstrainedText(
                    text = industry,
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textSecondary
                )
            }
            ClickableIcon(
                modifier = Modifier.align(Alignment.TopEnd),
                painter = painterResource(id = R.drawable.ic_favorite_16),
                contentDescription = stringResource(id = R.string.descriptionDefaultStock),
                tint = AppTheme.colors.iconActive,
                onClick = onFavoriteIconClick
            )
        }
    }
}