package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun StocksSectionHeader(
    modifier: Modifier = Modifier,
    title: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = APP_CONTENT_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textSecondary
        )
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(id = R.drawable.ic_down_24),
            contentDescription = stringResource(id = R.string.descriptionDecorativeArrowDown),
            tint = AppTheme.colors.buttonPrimary
        )
    }
}