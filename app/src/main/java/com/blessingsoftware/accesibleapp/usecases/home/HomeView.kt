package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.onSecondary)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .align(Alignment.CenterHorizontally),
                thickness = 7.dp,
                color = MaterialTheme.colors.secondary
            )
            if (selectedPlace != null) {
                Images(
                    viewModel = viewModel,
                    id = selectedPlace.placeImages,
                    modifier = Modifier.height(350.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))

                Column(Modifier.padding(horizontal = 10.dp)) {
                    ReusableTittle(
                        selectedPlace.placeName,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ReusableTextBody(selectedPlace.placeDescription)
                    Spacer(modifier = Modifier.height(10.dp))
                    ReusableSubtitle("Accesibilidades")
                    ReusableTextBody(selectedPlace.placeAccessibility)
                    Spacer(modifier = Modifier.height(10.dp))
                    ReusableSubtitle("Potenciales Dificultades")
                    ReusableTextBody(selectedPlace.placeDifficulties)
                    Spacer(modifier = Modifier.height(10.dp))
                    PlaceRate(suggestionRate = selectedPlace.placeRate)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = { Log.d("Boton", "Calificar") },
                            modifier = Modifier
                                .height(50.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                            )
                        ) {
                            Text(
                                "Calificar",
                                color = MaterialTheme.colors.onBackground,
                                modifier = Modifier,
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Button(
                            onClick = {
                                openGoogleMaps(
                                    context,
                                    selectedPlace.placeLat,
                                    selectedPlace.placeLng
                                )
                            },
                            modifier = Modifier
                                .height(50.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                            )
                        ) {
                            Text(
                                "Abrir en Uber/Bolt",
                                modifier = Modifier,
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                }
            }
        }

    }
}

fun openGoogleMaps(context: Context, placeLat: String, placeLng: String) {

    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$placeLat,$placeLng?z=18"))
    try {
        context.startActivity(mapIntent)
    } catch (s: SecurityException) {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG)
            .show()
    }
}


@Composable
private fun PlaceRate(suggestionRate: Int) {
    Spacer(modifier = Modifier.height(10.dp))
    Column(Modifier.fillMaxWidth()) {
        Text(
            "Calificación de Accesibilidad:",
            modifier = Modifier.align(Alignment.End),
            MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Right,
        )
        StarRate(
            rate = suggestionRate,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Arrangement.End,
            size = 40
        )
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


