package com.ferelin.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ferelin.core.ui.LOTTIE_SPLASH_ANIM
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun SplashScreen(
    onSplashTimeout: () -> Unit
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset(LOTTIE_SPLASH_ANIM)
    )
    val progress by animateLottieCompositionAsState(composition)

    LaunchedEffect(key1 = progress) {
        if (progress == 1f) {
            onSplashTimeout()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress
        )
    }
}