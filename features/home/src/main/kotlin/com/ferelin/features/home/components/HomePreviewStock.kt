package com.ferelin.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.GlideIcon
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun StockPreview(
    modifier: Modifier = Modifier,
    name: String,
    industry: String,
    isFavorite: Boolean,
    iconUrl: String
) {
    Surface(
        modifier = modifier.width(100.dp),
        elevation = 2.dp,
        color = AppTheme.colors.backgroundPrimary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 8.dp),
        ) {
            CompanyImageWithFavorite(
                iconUrl = iconUrl,
                isFavorite = isFavorite
            )
            Spacer(modifier = Modifier.height(4.dp))
            ConstrainedText(
                text = name,
                style = AppTheme.typography.body2,
                color = AppTheme.colors.textPrimary
            )
            ConstrainedText(
                text = industry,
                style = AppTheme.typography.body2,
                color = AppTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun CompanyImageWithFavorite(
    modifier: Modifier = Modifier,
    iconUrl: String,
    isFavorite: Boolean
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GlideIcon(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape),
            imageModel = iconUrl
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.ic_favorite_16),
            contentDescription = stringResource(
                id = if (isFavorite) {
                    R.string.descriptionFavoriteStock
                } else R.string.descriptionDefaultStock
            ),
            tint = if (isFavorite) {
                AppTheme.colors.iconActive
            } else {
                AppTheme.colors.iconDisabled
            }
        )
    }
}