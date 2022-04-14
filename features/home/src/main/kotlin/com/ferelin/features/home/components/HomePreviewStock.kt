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
import com.ferelin.core.ui.components.FailIcon
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun StockPreview(
    modifier: Modifier = Modifier,
    name: String,
    industry: String,
    isFavourite: Boolean,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape),
                    imageModel = iconUrl,
                    failure = { FailIcon() }
                )
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_favourite_16),
                    contentDescription = stringResource(
                        id = if (isFavourite) {
                            R.string.descriptionFavouriteStock
                        } else R.string.descriptionDefaultStock
                    ),
                    tint = if (isFavourite) {
                        AppTheme.colors.iconActive
                    } else {
                        AppTheme.colors.iconDisabled
                    }
                )
            }
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