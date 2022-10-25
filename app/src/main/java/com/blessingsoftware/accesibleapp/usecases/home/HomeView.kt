package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.model.domain.LocationDetails
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
    ) {
        MainMap(viewModel)
    }
}

@Composable
fun MainMap(viewModel: HomeViewModel) {



    //Lugares
    val places by viewModel.places.observeAsState(initial = emptyList())


    //Posicion inicial de la vista del mapa
    val cam = LatLng(-25.285971270337797, -57.59672128640907)
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 11f)
    }

    //Composable de Google Maps
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPosition) {
        places.forEach {
                place ->
            Log.d("NombreLugar", place.placeName)
            if (place.placeLat.isNotEmpty() && place.placeLng.isNotEmpty()) {
                val placePosition =
                    LatLng(place.placeLat.toDouble(), place.placeLng.toDouble())
                Marker(
                    position = placePosition,
                    title = place.placeName,
                    snippet = place.placeDescription
                )
            }
        }
    }
    //TODO Hacer un boton en el mapa que centre la camara en la ubicacion actual del usuario
}




@Composable
private fun HomeBackHandler() {
    val activity = (LocalContext.current as? Activity)
    androidx.activity.compose.BackHandler(enabled = true, onBack = {
        activity?.finish()

        Log.d("BackHandler", "Boton atras")
    })
}