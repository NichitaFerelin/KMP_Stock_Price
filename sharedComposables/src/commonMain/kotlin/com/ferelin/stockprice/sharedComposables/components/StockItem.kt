package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StockItem(
  modifier: Modifier = Modifier,
  index: Int,
  iconUrl: String,
  ticker: String,
  name: String,
  isFavourite: Boolean,
  onFavouriteIconClick: () -> Unit,
  onClick: () -> Unit
) {
  /*Card(
    modifier = modifier
      .fillMaxWidth()
      .height(75.dp),
    backgroundColor = if (index % 2 == 0) AppTheme.colors.contendPrimary else AppTheme.colors.contendSecondary,
    shape = RoundedCornerShape(12.dp),
    elevation = 0.dp
  ) {
    ConstraintLayout(
      modifier = modifier
        .fillMaxSize()
        .clickable(onClick = onClick)
    ) {
      val (icon, title, subtitle, star) = createRefs()
      val centerGuideline = createGuidelineFromTop(0.55f)

      GlideImage(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .constrainAs(icon) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, margin = HORIZONTAL_MARGIN)
          },
        imageModel = iconUrl,
        contentScale = ContentScale.Inside,
        failure = { FailIcon() }
      )
      ConstrainedText(
        modifier = Modifier.constrainAs(title) {
          bottom.linkTo(centerGuideline, margin = TEXT_SECTION_BETWEEN_MARGIN)
          start.linkTo(icon.end, margin = TEXT_SECTION_START_MARGIN)
          end.linkTo(star.start, margin = TEXT_SECTION_END_MARGIN)
          width = Dimension.fillToConstraints
        },
        text = ticker,
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
      ConstrainedText(
        modifier = Modifier.constrainAs(subtitle) {
          top.linkTo(centerGuideline, margin = TEXT_SECTION_BETWEEN_MARGIN)
          start.linkTo(icon.end, margin = TEXT_SECTION_START_MARGIN)
          end.linkTo(star.start, margin = TEXT_SECTION_END_MARGIN)
          width = Dimension.fillToConstraints
        },
        text = name,
        style = AppTheme.typography.body2,
        color = AppTheme.colors.textPrimary
      )
      ClickableIcon(
        modifier = Modifier.constrainAs(star) {
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
          end.linkTo(parent.end, margin = HORIZONTAL_MARGIN)
        },
        backgroundColor = if (index % 2 == 0) AppTheme.colors.contendPrimary else AppTheme.colors.contendSecondary,
        painter = painterResource(id = R.drawable.ic_favourite_16),
        contentDescription = ""*//*TODO*//*,
        iconTint = if (isFavourite) AppTheme.colors.iconActive else AppTheme.colors.iconDisabled,
        onClick = onFavouriteIconClick
      )
    }
  }*/
}

private val HORIZONTAL_MARGIN = 12.dp
private val TEXT_SECTION_START_MARGIN = 16.dp
private val TEXT_SECTION_END_MARGIN = 4.dp
private val TEXT_SECTION_BETWEEN_MARGIN = 1.dp