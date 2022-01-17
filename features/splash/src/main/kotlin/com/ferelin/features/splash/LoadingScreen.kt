package com.ferelin.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.splash.component.LoadingItem
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onNavigationRequested: () -> Unit) {
  val coroutineScope = rememberCoroutineScope()
  LaunchedEffect(key1 = coroutineScope) {
    onNavigationRequested()
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    LoadingItem(painter = painterResource(id = R.mipmap.msft))
    Spacer(modifier = Modifier.height(30.dp))
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