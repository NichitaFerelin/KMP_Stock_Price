@file:OptIn(ExperimentalMaterialApi::class)

package com.ferelin.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun PreviewHolder(
    modifier: Modifier = Modifier,
    title: String,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    innerContent: @Composable () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var topBarBackground by remember { mutableStateOf(TopBarBackground(screenWidth)) }

    LaunchedEffect(key1 = screenWidth.value) {
        topBarBackground = TopBarBackground(screenWidth)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = PREVIEW_MIN_HEIGHT),
        elevation = 0.dp,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary.copy(alpha = 0f),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppTheme.colors.backgroundSecondary,
                            AppTheme.colors.backgroundPrimary
                        ),
                        center = Offset(
                            x = LocalDensity.current.run { topBarBackground.radialXCenter.toPx() },
                            y = LocalDensity.current.run { topBarBackground.radialYCenter.toPx() }
                        ),
                        radius = LocalDensity.current.run { topBarBackground.gradientRadius.toPx() }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp)
                    .size(16.dp)
                    .align(Alignment.TopEnd),
                painter = painterResource(id = R.drawable.ic_forward_24),
                contentDescription = contentDescription,
                tint = AppTheme.colors.buttonPrimary
            )
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = stringResource(id = R.string.descriptionStockIcon),
                        tint = AppTheme.colors.buttonPrimary
                    )
                    Spacer(modifier = modifier.width(12.dp))
                    Text(
                        text = title,
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.textPrimary
                    )
                }

                innerContent()

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Immutable
private data class TopBarBackground(
    val screenWidth: Dp,
    val boxWidth: Dp = screenWidth - APP_CONTENT_PADDING / 2,
    val radialYCenter: Dp = PREVIEW_MIN_HEIGHT / 5,
    val radialXCenter: Dp = boxWidth - boxWidth / 4,
    val gradientRadius: Dp = boxWidth / 2
)

private val PREVIEW_MIN_HEIGHT = 80.dp