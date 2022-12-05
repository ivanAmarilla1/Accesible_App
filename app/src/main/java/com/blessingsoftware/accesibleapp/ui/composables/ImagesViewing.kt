package com.blessingsoftware.accesibleapp.ui.composables

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.*
import com.blessingsoftware.accesibleapp.model.domain.ImageList
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder

@Composable
fun Images(
    viewModel: HomeViewModel,
    id: String,
    modifier: Modifier = Modifier
) {
    val imageList = ImageList()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp


    LaunchedEffect(id) {
        viewModel.setImageListEmpty()
        viewModel.getPlaceImages(id)
    }
    val images = viewModel.imageList.observeAsState()

    if (images.value != null) {
        imageList.listOfSelectedImages = images.value!!
    }

    Column(
        modifier = modifier
            .padding(top = 20.dp, bottom = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.5f)
        ) {
            if (imageList.listOfSelectedImages.isEmpty()) {
                ImagePlaceHolder(height = screenHeight * 0.5f, width = screenWidth * 0.6f)
            } else {
                LazyRow(
                    modifier = Modifier
                ) {
                    itemsIndexed(imageList.listOfSelectedImages) { index, uri ->
                        ImageItem(
                            uri = uri,
                            height = screenHeight * 0.5f,
                            width = screenWidth * 0.6f,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageItem(
    uri: Uri,
    height: Dp,
    width: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SubcomposeAsyncImage(
            model = uri,
            modifier = Modifier
                .width(width)
                .height(height),
            loading = {
                rememberAsyncImagePainter(ImagePlaceHolderTwo(height = height * 0.5f, width = width * 0.6f))
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ImagePlaceHolder(height: Dp, width: Dp) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            Modifier
                .width(width)
                .height(height)
                .placeholder(
                    visible = true,
                    color = MaterialTheme.colors.surface,
                    highlight = PlaceholderHighlight.fade(MaterialTheme.colors.secondaryVariant),
                )
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            Modifier
                .width(width)
                .height(height)
                .placeholder(
                    visible = true,
                    color = MaterialTheme.colors.surface,
                    highlight = PlaceholderHighlight.fade(MaterialTheme.colors.secondaryVariant),
                )
        ) {}
    }
}

@Composable
private fun ImagePlaceHolderTwo(height: Dp, width: Dp) {
        Box(
            Modifier
                .width(width)
                .height(height)
                .placeholder(
                    visible = true,
                    color = MaterialTheme.colors.surface,
                    highlight = PlaceholderHighlight.fade(MaterialTheme.colors.secondaryVariant),
                )
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
}


