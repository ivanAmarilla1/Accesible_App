package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {
    Box(modifier = Modifier.padding(bottom = 50.dp)) {
        PlaceBottomDrawer(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlaceBottomDrawer(viewModel: HomeViewModel) {
    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedPlace = viewModel.selectedPlace.observeAsState()


    //TODO Centrar la camara en el marcador seleccionado

    BottomDrawer(
        drawerState = bottomDrawerState,
        drawerContent = {
            DrawerContent(selectedPlace.value)
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
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPosition) {
        places.forEach { place ->
            Log.d("NombreLugar", place.placeName)
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
private fun DrawerContent(selectedPlace: Place?) {
    Column(
        Modifier
            .background(MaterialTheme.colors.onSecondary)
            .fillMaxWidth()
            .height(400.dp)
    ) {
        if (selectedPlace != null) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                selectedPlace.placeName,
                modifier = Modifier.fillMaxWidth(0.9f),
                MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                selectedPlace.placeDescription,
                modifier = Modifier.fillMaxWidth(0.9f),
                MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
            )


        }

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


