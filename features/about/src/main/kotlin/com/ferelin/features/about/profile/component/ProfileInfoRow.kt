package com.ferelin.features.about.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ferelin.core.ui.component.ConstrainedText

@Composable
internal fun ProfileInfoRow(
  modifier: Modifier = Modifier,
  name: String,
  content: String
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      modifier = Modifier.weight(1f),
      text = name
    )
    ConstrainedText(
      modifier = Modifier.weight(1f),
      text = content
    )
  }
}