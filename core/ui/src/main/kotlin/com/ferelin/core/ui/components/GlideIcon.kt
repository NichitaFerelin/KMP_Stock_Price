package com.ferelin.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun GlideIcon(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Inside,
    imageModel: Any?
) {
    GlideImage(
        modifier = modifier,
        imageModel = imageModel,
        contentScale = contentScale,
        failure = { FailIcon() }
    )
}