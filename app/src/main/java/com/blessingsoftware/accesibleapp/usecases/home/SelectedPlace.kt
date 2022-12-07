package com.blessingsoftware.accesibleapp.usecases.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.ui.composables.MySelectedPlace

@Composable
fun SelectedPlace(viewModel: HomeViewModel, navController: NavHostController) {
    val selectedPlace = viewModel.selectedSearchedPlace.observeAsState()
    val context = LocalContext.current

    MySelectedPlace(selectedPlace = selectedPlace.value, viewModel = viewModel, context = context)
}