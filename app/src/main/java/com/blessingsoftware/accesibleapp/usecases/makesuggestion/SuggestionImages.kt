package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SuggestionImages(viewModel: MakeSuggestionViewModel) {


    val context = LocalContext.current
    val state = viewModel.state
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) {
            viewModel.updateSelectedImageList(listOfImages = it, context)
        }

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    SideEffect {
        permissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.5f)
        ) {
            if (state.listOfSelectedImages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Seleccione una imágen")

                }
            }
            LazyRow(
                modifier = Modifier
            ) {
                if (state.listOfSelectedImages.isNotEmpty()) {
                    itemsIndexed(state.listOfSelectedImages) { index, uri ->
                        ImagePreviewItem(uri = uri,
                            height = screenHeight * 0.5f,
                            width = screenWidth * 0.6f,
                            onClick = { viewModel.onItemRemove(index) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            onClick = {
                if (permissionState.status.isGranted) {
                    galleryLauncher.launch("image/*")
                } else
                    permissionState.launchPermissionRequest()
            }) {
            Text(text = "Selecionar Imagen", color = MaterialTheme.colors.onBackground)
        }
    }
}


@Composable
private fun ImagePreviewItem(
    uri: Uri,
    height: Dp,
    width: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "",
            modifier = Modifier
                .width(width)
                .height(height),
            contentScale = ContentScale.Crop
        )

        IconButton(onClick = { onClick() }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "",
                modifier = Modifier
                    .size(45.dp),
                tint = Color.Red
            )
        }
    }
}