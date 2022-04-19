package com.ferelin.features.about.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.R

@Composable
internal fun ProfitValue(
    value: String,
    lceState: LceState,
    fetchLceState: LceState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (lceState) {
            is LceState.Content -> {
                val isProfitPositive = value.isNotEmpty() && value[0] == '+'
                Text(
                    text = value,
                    style = AppTheme.typography.body1,
                    color = if (isProfitPositive) {
                        AppTheme.colors.textPositive
                    } else AppTheme.colors.textNegative
                )

                if (fetchLceState is LceState.Loading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AppCircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
            is LceState.Loading -> {
                AppCircularProgressIndicator()
            }
            is LceState.Error -> {
                Text(
                    text = stringResource(id = R.string.errorDownload),
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textPrimary
                )
            }
            else -> Unit
        }
    }
}