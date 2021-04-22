package com.ferelin.stockprice.ui.previewSection.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.R
import com.ferelin.stockprice.utils.compose.*

@Composable
@Preview
fun Welcome(viewModel: WelcomeViewModel = WelcomeViewModel()) {

    val mainTitleVisible by viewModel.mainTitleVisible.observeAsState(initial = false)
    val hintChartSectionVisible by viewModel.hintChartSectionVisible.observeAsState(initial = false)
    val hintFavouriteVisible by viewModel.hintFavouriteVisible.observeAsState(initial = false)
    val hintApiLimitVisible by viewModel.hintApiLimitVisible.observeAsState(initial = false)
    val btnDoneVisible by viewModel.btnDoneVisible.observeAsState(initial = false)

    LazyColumn(
        content = {
            item {
                Card(visible = mainTitleVisible) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = hintAppTitle,
                        style = StockPriceTypography.h1,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
            item {
                Card(visible = hintFavouriteVisible) {
                    Hint(
                        title = hintFavouriteTitle,
                        content = hintFavouriteContent,
                        imageSource = painterResource(id = R.mipmap.error),
                        contentDescription = hintFavouriteContentDescription
                    )
                }
            }
            item {
                Card(visible = hintChartSectionVisible) {
                    Hint(
                        title = hintChartTitle,
                        content = "",
                        imageSource = painterResource(id = R.mipmap.chart),
                        contentDescription = hintChartContentDescription
                    )
                }
            }
            item {
                Card(visible = hintApiLimitVisible) {
                    Hint(
                        title = hintApiTitle,
                        content = hintApiContent,
                        imageSource = painterResource(id = R.drawable.ic_favourite),
                        contentDescription = hintApiContentDescription
                    )
                }
            }
        })

    AnimatedVisibility(visible = btnDoneVisible) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            FloatingActionButton(
                backgroundColor = StockPriceColors.onPrimary,
                modifier = Modifier.padding(12.dp),
                onClick = {
                    viewModel.onBtnClicked()
                },
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_done),
                        contentDescription = null
                    )
                })
        }
    }
}

@Composable
fun Hint(
    title: String,
    content: String,
    imageSource: Painter,
    contentDescription: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp),
        shape = StockPriceShapes.large,
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = title,
                style = StockPriceTypography.h2
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = content,
                style = StockPriceTypography.h3
            )
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier.padding(top = 8.dp),
                    painter = imageSource,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun Card(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        exit = slideOut(targetOffset = {
            IntOffset(0, -it.height)
        }) + fadeOut()
    ) {
        content.invoke()
    }
}