package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity
import android.content.Context

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.PlaceTypes
import com.blessingsoftware.accesibleapp.ui.composables.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {
    Box(modifier = Modifier.background(MaterialTheme.colors.onSecondary)) {
        PlaceBottomDrawer(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeViewBackHandler(
    bottomDrawerState: BottomDrawerState,
    scope: CoroutineScope
) {
    if (bottomDrawerState.isOpen) {
        BackHandler(enabled = true, onBack = {
            scope.launch {
                bottomDrawerState.close()
            }
        })
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

    val context = LocalContext.current

    HomeViewBackHandler(bottomDrawerState, scope)

    //TODO Centrar la camara en el marcador seleccionado
    BottomDrawer(
        drawerState = bottomDrawerState,
        drawerContent = {
            DrawerContent(selectedPlace.value, viewModel)
        },
        gesturesEnabled = bottomDrawerState.isOpen
    ) {
        MainMap(viewModel = viewModel, selectedPlace = selectedPlace.value, context) {
            scope.launch {
                viewModel.setSelectedPlace(it)
                bottomDrawerState.open()
            }
            return@MainMap bottomDrawerState.isClosed
        }
    }
}


@Composable
fun MainMap(viewModel: HomeViewModel, selectedPlace: Place?, context: Context, onMarkerClicked: (Place) -> Boolean) {
    //Lugares
    val places by viewModel.places.observeAsState(initial = emptyList())
    //Posicion inicial de la vista del mapa
    val cam = LatLng(-25.286863187452415, -57.65103018717416)
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 14f)
    }
    //Cada que cambia el lugar seleccionado, la posicion de la camara se actualiza para centrarse en el lugar
    if (selectedPlace != null) {
        LaunchedEffect(key1 = selectedPlace) {
            //Log.d("Entro aca", "si ${selectedPlace.placeId}")
            cameraPosition.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        LatLng(
                            selectedPlace.placeLat.toDouble(),
                            selectedPlace.placeLng.toDouble()
                        ), 15f
                    )
                )
            )
        }
    }
    //propiedades y UI del mapa
    val mapSettings = if (isSystemInDarkTheme()) R.raw.nightmapsettings else R.raw.standardmapsettings
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapSettings)
            )
        )
    }
    uiSettings = uiSettings.copy(zoomControlsEnabled = false)

    //Composable de Google Maps
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        cameraPositionState = cameraPosition,
        uiSettings = uiSettings,
        properties = properties

    ) {
        places.forEach { place ->
            val placeType = getPlaceType(place)
            if (place.placeLat.isNotEmpty() && place.placeLng.isNotEmpty()) {
                val placePosition =
                    LatLng(place.placeLat.toDouble(), place.placeLng.toDouble())

                MyMarker(
                    position = placePosition,
                    title = place.placeName,
                    visible = cameraPosition.position.zoom >= 14f,
                    snippet = place.placeDescription,
                    alpha = 0.5f,
                    icon = bitmapDescriptor(context, placeType.placeIcon()),
                ) {
                    onMarkerClicked(place)
                }
                /*Marker(
                    position = placePosition,
                    icon = bitmapDescriptor(context, R.drawable.entertaiment_icon),
                    title = place.placeName,
                    visible = cameraPosition.position.zoom >= 15f,
                    snippet = place.placeDescription,
                    onClick = {
                        onMarkerClicked(place)
                    }
                )*/

            }
        }
    }
}


fun getPlaceType(place: Place): PlaceTypes {
    val myPlaceType = place.placeType
    val placeTypes = PlaceTypes.values()
    for (type in placeTypes) {
        if (type.description() ==  myPlaceType) {
            return type
        }
    }
    return PlaceTypes.OTROS
}


@Composable
fun MyMarker(
    position: LatLng,
    title: String,
    visible: Boolean,
    snippet: String?,
    alpha: Float,
    icon: BitmapDescriptor?,
    onMarkerClicked: () -> Boolean
){
    Marker(
        position = position,
        icon = icon,
        title = title,
        snippet = snippet,
        //alpha = alpha,
        visible = visible,
        onClick = { onMarkerClicked() }
    )
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


