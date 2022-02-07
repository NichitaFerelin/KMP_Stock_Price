package com.ferelin.features.stocks.uiComponents

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

internal val CRYPTO_HEIGHT = 90.dp

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
      .width(120.dp)
      .height(CRYPTO_HEIGHT),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    elevation = 6.dp
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceAround
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        ConstrainedText(
          text = name,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textPrimary
        )
        GlideImage(
          modifier = Modifier.size(20.dp),
          imageModel = iconUrl,
          failure = { FailIcon() }
        )
      }
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