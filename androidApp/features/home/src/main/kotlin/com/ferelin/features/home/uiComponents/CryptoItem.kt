package com.ferelin.features.home.uiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.FailIcon
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
internal fun CryptoItem(
  modifier: Modifier = Modifier,
  name: String,
  iconUrl: String,
  price: String,
  profit: String
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(100.dp)
      .padding(horizontal = 12.dp),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    elevation = 6.dp
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      GlideImage(
        modifier = Modifier.size(40.dp),
        imageModel = iconUrl,
        failure = { FailIcon() }
      )
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
      ) {
        ConstrainedText(
          text = name,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textPrimary
        )
        ConstrainedText(
          text = price,
          style = AppTheme.typography.body1,
          color = AppTheme.colors.textPrimary
        )
        ConstrainedText(
          text = profit,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textPrimary
        )
      }
    }
  }
}

@Preview
@Composable
private fun CryptoItemLight() {
  AppTheme(useDarkTheme = false) {
    CryptoItem(
      name = "Bitcoin",
      iconUrl = "",
      price = "43 333 $",
      profit = "+1333 $"
    )
  }
}

@Preview
@Composable
private fun CryptoItemDark() {
  AppTheme(useDarkTheme = true) {
    CryptoItem(
      name = "Bitcoin",
      iconUrl = "",
      price = "43 333 $",
      profit = "+1333 $"
    )
  }
}

@Preview
@Composable
private fun CryptoItemLongText() {
  AppTheme {
    CryptoItem(
      name = "AaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAa",
      iconUrl = "",
      price = "fffffffffffffffffffffffffffffffffffffffffffffffff",
      profit = "pppppppppppppppppppppppppppppppppppppppppppppppppp"
    )
  }
}