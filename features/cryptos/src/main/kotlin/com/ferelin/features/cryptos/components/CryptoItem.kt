package com.ferelin.features.cryptos.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.components.GlideIcon
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun CryptoItem(
    modifier: Modifier = Modifier,
    name: String,
    logoUrl: String,
    price: String,
    profit: String
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
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideIcon(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                imageModel = logoUrl,
                contentScale = ContentScale.Fit
            )
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = name,
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textPrimary
                )
                Text(
                    text = price,
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textSecondary
                )
                Text(
                    text = profit,
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textSecondary
                )
            }
        }
    }
}