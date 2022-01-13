package com.ferelin.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.splash.R
import com.ferelin.features.splash.component.LoadingItem
import com.google.accompanist.insets.statusBarsPadding

@Composable
internal fun LoadingScreen() {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row {
      LoadingItem(painter = painterResource(R.mipmap.facebook))
      Spacer(modifier = Modifier.width(30.dp))
      LoadingItem(painter = painterResource(R.mipmap.tesla))
    }
    Spacer(modifier = Modifier.height(30.dp))
    Row {
      LoadingItem(painter = painterResource(R.mipmap.amazon))
      Spacer(modifier = Modifier.width(30.dp))
      LoadingItem(painter = painterResource(R.mipmap.eric))
      Spacer(modifier = Modifier.width(30.dp))
      LoadingItem(painter = painterResource(R.mipmap.zoom))
    }
  }
}