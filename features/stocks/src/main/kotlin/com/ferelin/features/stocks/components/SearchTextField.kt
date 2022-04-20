package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.TextField
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SearchTextField(
    modifier: Modifier = Modifier,
    searchRequest: String,
    onSearchRequestsChanged: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                inputValue = searchRequest,
                placeholder = stringResource(id = R.string.hintSearch),
                onValueChange = onSearchRequestsChanged,

                )
        }
    }
}