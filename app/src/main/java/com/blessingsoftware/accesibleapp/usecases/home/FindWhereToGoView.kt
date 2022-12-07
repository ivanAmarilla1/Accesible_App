package com.blessingsoftware.accesibleapp.usecases.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.model.domain.PlaceTypes
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun FindWhereToGoView(viewModel: HomeViewModel, navController: NavController) {
    val placeTypes = PlaceTypes.values()

    Box(modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 50.dp)) {
        TypesLazyVerticalGrid (placeTypes) {
            viewModel.setSelectedPlaceType(it)
            navController.navigate(AppScreens.PlaceTypeSelected.route) {
                launchSingleTop = true
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TypesLazyVerticalGrid(placeTypes: Array<PlaceTypes>, onClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        // content padding
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        content = {
            items(PlaceTypes.values().size) { index ->
                val x = placeTypes[index].description()
                Card(
                    backgroundColor = Color.Red,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .height(200.dp),
                    elevation = 8.dp,
                    onClick = {onClick(x)}
                ) {
                    Text(
                        text = x,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}

