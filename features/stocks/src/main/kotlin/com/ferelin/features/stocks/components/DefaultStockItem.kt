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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.FailIcon
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun DefaultStockItem(
    modifier: Modifier = Modifier,
    ticker: String,
    name: String,
    logoUrl: String,
    index: Int,
    onClick: () -> Unit,
    onFavoriteIconClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = APP_CONTENT_PADDING),
        shape = RoundedCornerShape(6.dp),
        elevation = 0.dp,
        backgroundColor = if (index % 2 == 0) {
            AppTheme.colors.contendPrimary
        } else AppTheme.colors.contendTertiary,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            GlideImage(
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(6.dp)),
                imageModel = logoUrl,
                contentScale = ContentScale.Inside,
                failure = { FailIcon() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.7f),
                    verticalArrangement = Arrangement.Center
                ) {
                    ConstrainedText(
                        text = ticker,
                        style = AppTheme.typography.body2,
                        color = AppTheme.colors.textPrimary
                    )
                    ConstrainedText(
                        text = name,
                        style = AppTheme.typography.body2,
                        color = AppTheme.colors.textPrimary
                    )
                }
                ClickableIcon(
                    painter = painterResource(id = R.drawable.ic_favorite_16),
                    contentDescription = stringResource(id = R.string.descriptionDefaultStock),
                    tint = AppTheme.colors.iconDisabled,
                    onClick = onFavoriteIconClick
                )
            }
        }
    }
}
