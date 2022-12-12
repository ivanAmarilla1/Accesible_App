package com.blessingsoftware.accesibleapp.ui.composables

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun MySelectedPlace(selectedPlace: Place?, viewModel: HomeViewModel, context: Context) {
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
                ReusableTextBody(returnOrderedString(selectedPlace.placeAccessibility, stringResource(R.string.emoji_check)))
                Spacer(modifier = Modifier.height(20.dp))
                ReusableSubtitle("Potenciales Dificultades")
                ReusableTextBody(returnOrderedString(selectedPlace.placeDifficulties, stringResource(R.string.emoji_delete)
                ))
                Spacer(modifier = Modifier.height(10.dp))
                PlaceRate(suggestionRate = selectedPlace.placeRate)
            }
            Images(
                viewModel = viewModel,
                id = selectedPlace.placeImages,
                modifier = Modifier.height(350.dp)
            )
            //Spacer(modifier = Modifier.height(15.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { Log.d("Boton", "Calificar") },
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


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MySearchedPlace(selectedPlace: Place?, viewModel: HomeViewModel, context: Context, navController: NavController) {
    Scaffold(
        topBar = { MyTopBar(navController, selectedPlace?.placeName ?: "") },
        bottomBar = { MySearchedPlaceBottomBar(selectedPlace, context) {
            navController.navigate(AppScreens.HomeView.route){
                popUpTo(AppScreens.HomeView.route) {
                    saveState = true
                }
                launchSingleTop = true
            }
            viewModel.setSelectedPlace(it)
            viewModel.setBottomBarVisible(true)
        } }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            if (selectedPlace != null) {
                Column(Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    ReusableTextBody(selectedPlace.placeDescription)
                    Spacer(modifier = Modifier.height(20.dp))
                    ReusableSubtitle("Accesibilidades")
                    ReusableTextBody(returnOrderedString(selectedPlace.placeAccessibility, stringResource(R.string.emoji_check)))
                    Spacer(modifier = Modifier.height(20.dp))
                    ReusableSubtitle("Potenciales Dificultades")
                    ReusableTextBody(returnOrderedString(selectedPlace.placeDifficulties, stringResource(R.string.emoji_delete)
                    ))
                    Spacer(modifier = Modifier.height(10.dp))
                    PlaceRate(suggestionRate = selectedPlace.placeRate)
                }
                Images(
                    viewModel = viewModel,
                    id = selectedPlace.placeImages,
                    modifier = Modifier.height(350.dp)
                )
                //Spacer(modifier = Modifier.height(15.dp))
                Spacer(modifier = Modifier.height(15.dp))
            } else {
                Text(text = "Ha ocurrido un error")
            }
        }
    }
}

@Composable
private fun MySearchedPlaceBottomBar(selectedPlace: Place?, context: Context, onOpenMapClick: (Place) -> Unit) {
    if (selectedPlace!=null) {
        BottomNavigation (
            modifier = Modifier.height(70.dp),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { onOpenMapClick(selectedPlace, ) },
                    modifier = Modifier
                        .width(160.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                    )
                ) {
                    Text(
                        "Ver en el Mapa",
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