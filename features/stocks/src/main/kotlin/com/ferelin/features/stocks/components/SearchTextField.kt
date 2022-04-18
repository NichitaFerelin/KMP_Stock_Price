package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.TextField
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SearchTextField(
    modifier: Modifier = Modifier,
    searchRequest: String,
    onSearchRequestsChanged: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            ClickableIcon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_back_24),
                tint = AppTheme.colors.buttonPrimary,
                contentDescription = stringResource(id = R.string.descriptionBack),
                onClick = onBackClick
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextField(
                inputValue = searchRequest,
                placeholder = stringResource(id = R.string.hintSearch),
                onValueChange = onSearchRequestsChanged,

            )
        }
    }
}