package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.ui.composables.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {

    Box(modifier = Modifier.background(MaterialTheme.colors.onSecondary)) {
        PlaceBottomDrawer(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlaceBottomDrawer(viewModel: HomeViewModel) {
    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedPlace = viewModel.selectedPlace.observeAsState()

    //Muestra y oculta el Bottom drawer (botones de buscar y home de la parte de abajo) si se abre el modal
    if (bottomDrawerState.isOpen) {
        viewModel.setBottomBarVisible(false)
    } else {
        viewModel.setBottomBarVisible(true)
    }


    //TODO Centrar la camara en el marcador seleccionado

    BottomDrawer(
        //modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        drawerState = bottomDrawerState,
        drawerContent = {
            DrawerContent(selectedPlace.value, viewModel)
        },
        gesturesEnabled = bottomDrawerState.isOpen
    ) {
        MainMap(viewModel = viewModel) {
            scope.launch {
                viewModel.setSelectedPlace(it)
                bottomDrawerState.open()
            }
            return@MainMap bottomDrawerState.isClosed
        }
    }
}


@Composable
fun MainMap(viewModel: HomeViewModel, onMarkerClicked: (Place) -> Boolean) {
    //Lugares
    val places by viewModel.places.observeAsState(initial = emptyList())
    //Posicion inicial de la vista del mapa
    val cam = LatLng(-25.286863187452415, -57.65103018717416)
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 14f)
    }
    //Composable de Google Maps
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp), cameraPositionState = cameraPosition
    ) {
        places.forEach { place ->
            if (place.placeLat.isNotEmpty() && place.placeLng.isNotEmpty()) {
                val placePosition =
                    LatLng(place.placeLat.toDouble(), place.placeLng.toDouble())

                MarkerInfoWindow(
                    position = placePosition,
                    title = place.placeName,
                    snippet = place.placeDescription,
                    onClick = { onMarkerClicked(place) }
                ) {

                }
            }
        }
    }
    //TODO Hacer un boton en el mapa que centre la camara en la ubicacion actual del usuario
}


@Composable
private fun DrawerContent(
    selectedPlace: Place?,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current

    Surface(
        Modifier
            .background(MaterialTheme.colors.onSecondary)
            .fillMaxWidth()
            .height(600.dp)
    ) {
        MySelectedPlace(selectedPlace, viewModel, context)
    }
}

@Composable
private fun HomeBackHandler() {
    val activity = (LocalContext.current as? Activity)
    androidx.activity.compose.BackHandler(enabled = true, onBack = {
        activity?.finish()

        Log.d("BackHandler", "Boton atras")
    })
}


