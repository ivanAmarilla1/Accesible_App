package com.blessingsoftware.accesibleapp.ui.composables

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MySelectedPlace(selectedPlace: Place?, viewModel: HomeViewModel, context: Context, onRatePlaceButtonClicked: () -> Unit) {
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
                Spacer(modifier = Modifier.height(20.dp))
                ReusableSubtitle("Accesibilidades")
                ReusableTextBody(
                    returnOrderedString(
                        selectedPlace.placeAccessibility,
                        stringResource(R.string.emoji_check)
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                ReusableSubtitle("Potenciales Dificultades")
                ReusableTextBody(
                    returnOrderedString(
                        selectedPlace.placeDifficulties, stringResource(R.string.emoji_delete)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                PlaceRate(suggestionRate = selectedPlace.placeRate, selectedPlace.placeNumberOfRaters)
            }
            Images(
                viewModel = viewModel,
                id = selectedPlace.placeImages,
                modifier = Modifier.height(350.dp)
            )
            //Spacer(modifier = Modifier.height(15.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { onRatePlaceButtonClicked() },
                    modifier = Modifier
                        .width(160.dp)
                        .height(60.dp),
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
                        .width(160.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                    )
                ) {
                    Text(
                        "Navegar con otra App",
                        modifier = Modifier,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        } else {
            Text(text = "Ha ocurrido un error")
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MySearchedPlace(
    selectedPlace: Place?,
    viewModel: HomeViewModel,
    context: Context,
    navController: NavController
) {

    var columnScrollingEnabled by remember { mutableStateOf(true) }

    //posicion para el mapa
    val suggestionPosition = LatLng(
        selectedPlace?.placeLat?.toDouble() ?: 0.0,
        selectedPlace?.placeLng?.toDouble() ?: 0.0,
    )

    //posicion de la camara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(suggestionPosition, 16f)
    }

    //Para activar y desactivar el scroll cuando se mueve la camara del mapa
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScrollingEnabled = true
            //Log.d(ContentValues.TAG, "Map camera stopped moving - Enabling column scrolling...")
        }
    }
    Scaffold(
        topBar = { MyTopBar(navController, selectedPlace?.placeName ?: "") },
        bottomBar = { MySearchedPlaceBottomBar(selectedPlace, context) }
    ) {
        Column(
            modifier = Modifier
                //.padding(bottom = 50.dp)
                .verticalScroll(rememberScrollState(), columnScrollingEnabled)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            if (selectedPlace != null) {
                Column(Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    ReusableTextBody(selectedPlace.placeDescription)
                    Spacer(modifier = Modifier.height(20.dp))
                    ReusableSubtitle("Accesibilidades")
                    ReusableTextBody(
                        returnOrderedString(
                            selectedPlace.placeAccessibility,
                            stringResource(R.string.emoji_check)
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ReusableSubtitle("Potenciales Dificultades")
                    ReusableTextBody(
                        returnOrderedString(
                            selectedPlace.placeDifficulties, stringResource(R.string.emoji_delete)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PlaceRate(
                        suggestionRate = selectedPlace.placeRate,
                        selectedPlace.placeNumberOfRaters
                    )
                }
                Images(
                    viewModel = viewModel,
                    id = selectedPlace.placeImages,
                    modifier = Modifier.height(350.dp)
                )
                //Spacer(modifier = Modifier.height(15.dp))
                Spacer(modifier = Modifier.height(15.dp))
                DefaultMapMarker(
                    selectedPlace.placeLat.toDouble(),
                    selectedPlace.placeLng.toDouble(),
                    cameraPositionState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .testTag("Map")
                        .pointerInteropFilter(
                            onTouchEvent = {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        columnScrollingEnabled = false
                                        false
                                    }
                                    else -> {
                                        Log.d(
                                            ContentValues.TAG,
                                            "MotionEvent ${it.action} - this never triggers."
                                        )
                                        true
                                    }
                                }
                            }
                        )
                )
                Spacer(modifier = Modifier.height(70.dp))
            } else {
                Text(text = "Ha ocurrido un error")
            }
        }
    }
}

@Composable
private fun MySearchedPlaceBottomBar(selectedPlace: Place?, context: Context) {
    if (selectedPlace != null) {
        BottomNavigation(
            modifier = Modifier.height(70.dp),
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { //TODO
                    },
                    modifier = Modifier
                        .width(160.dp)
                        .height(60.dp),
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

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        openGoogleMaps(
                            context,
                            selectedPlace.placeLat,
                            selectedPlace.placeLng
                        )
                    },
                    modifier = Modifier
                        .width(160.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                    )
                ) {
                    Text(
                        "Navegar con otra App",
                        modifier = Modifier,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


private fun returnOrderedString(text: String, emoji: String): String {
    var orderedString = ""
    for (i in text.indices) {
        if (text[i] == '-') {
            orderedString += if (i != 0) {
                "\n$emoji"
            } else {
                emoji
            }
        } else {
            orderedString += text[i]
        }
    }
    return orderedString
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
private fun PlaceRate(suggestionRate: Double, placeNumberOfRaters: Int) {
    val suggestionRateRounded = String.format("%.1f", suggestionRate).toDouble()//Muestra la calificacion con solo 1 numero decimal
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
            rate = suggestionRate.toInt(),
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Arrangement.End,
            size = 40
        )
        Text(
            "($suggestionRateRounded de 5 con $placeNumberOfRaters calificaciones)",
            modifier = Modifier.align(Alignment.End),
            MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Right,
        )
    }
}