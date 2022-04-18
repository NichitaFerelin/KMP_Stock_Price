@file:OptIn(ExperimentalMaterialApi::class)

package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SearchField(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary,
        onClick = onSearchClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_search_17x18),
                tint = AppTheme.colors.buttonPrimary,
                contentDescription = stringResource(id = R.string.descriptionSearch)
            )
            Spacer(modifier = Modifier.width(16.dp))
            ConstrainedText(
                text = stringResource(id = R.string.descriptionSearch),
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textPrimary
            )
        }
    }
}