package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.params.ProfileParams
import com.ferelin.stockprice.shared.ui.viewModel.ProfileStateUi
import com.ferelin.stockprice.shared.ui.viewModel.ProfileViewModel
import com.ferelin.stockprice.sharedComposables.components.ProfileInfoColumn
import com.ferelin.stockprice.sharedComposables.components.ProfileInfoColumnClickable
import com.ferelin.stockprice.sharedComposables.components.ProfileInfoRow
import com.ferelin.stockprice.sharedComposables.components.ProfileInfoRowClickable
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun ProfileScreenRoute(
  profileParams: ProfileParams
) {
  val viewModelScope = rememberCoroutineScope()
  val viewModel: ProfileViewModel = remember {
    ViewModelWrapper().viewModel(viewModelScope, profileParams)
  }
  val uiState by viewModel.uiState.collectAsState()

  ProfileScreen(uiState)
}

@Composable
private fun ProfileScreen(
  uiState: ProfileStateUi
) {
  Column(
    modifier = Modifier.verticalScroll(
      state = rememberScrollState()
    ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(12.dp))
    Icon(
      imageVector = Icons.Default.Info,
      contentDescription = "Company information",
      tint = Color.Blue
    )
    Spacer(modifier = Modifier.height(12.dp))
    ProfileInfoColumn(
      name = "Name: ",
      content = uiState.profile.companyName
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (uiState.profile.webUrl.isNotEmpty()) {
      ProfileInfoColumnClickable(
        name = "Web url: ",
        content = uiState.profile.webUrl,
        onClick = { /**/ }
      )
      Spacer(modifier = Modifier.height(6.dp))
    }
    Divider(
      modifier = Modifier.fillMaxWidth(),
      color = AppTheme.colors.contendSecondary
    )
    Spacer(modifier = Modifier.height(6.dp))

    if (uiState.profile.country.isNotEmpty()) {
      ProfileInfoRow(
        name = "Country: ",
        content = uiState.profile.country
      )
      Spacer(modifier = Modifier.height(14.dp))
    }
    if (uiState.profile.industry.isNotEmpty()) {
      ProfileInfoRow(
        name = "Industry: ",
        content = uiState.profile.industry
      )
      Spacer(modifier = Modifier.height(14.dp))
    }
    if (uiState.profile.phone.isNotEmpty()) {
      ProfileInfoRowClickable(
        name = "Phone: ",
        content = uiState.profile.phone,
        onClick = { /**/ }
      )
      Spacer(modifier = Modifier.height(14.dp))
    }
    if (uiState.profile.capitalization.isNotEmpty()) {
      ProfileInfoRow(
        name = "Capitalization: ",
        content = uiState.profile.capitalization
      )
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}