package com.ferelin.features.about.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun ProfileRow(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(6.dp))

        val textModifier = if (onClick != null) {
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClick() }
        } else {
            Modifier
        }

        ConstrainedText(
            modifier = textModifier,
            text = value,
            style = AppTheme.typography.body1,
            color = if (onClick != null) {
                AppTheme.colors.textClickable
            } else {
                AppTheme.colors.textPrimary
            }
        )
    }
}