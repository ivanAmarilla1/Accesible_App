package com.blessingsoftware.accesibleapp.usecases.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.MyTopBar
import com.blessingsoftware.accesibleapp.ui.composables.StarRate
import com.blessingsoftware.accesibleapp.ui.composables.SuggestionPlaceImage
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PlaceTypeSelected(viewModel: HomeViewModel, navController: NavController) {

    val selectedPlaceType = viewModel.selectedPlaceType.observeAsState(initial = "")

    Scaffold(
        topBar = {MyTopBar(navController, selectedPlaceType.value)}
    ) {
        PlaceSelection(viewModel = viewModel, navController = navController, selectedPlaceType)
    }
}


@Composable
private fun PlaceSelection(
    viewModel: HomeViewModel,
    navController: NavController,
    selectedPlaceType: State<String>
) {
    val searchedPlaces by viewModel.searchedPlaces.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = selectedPlaceType.value) {//Cada vez que cambia la key se ejecuta el launched effect
        viewModel.getSeletedPlaces(selectedPlaceType.value)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val getPlacesFlow = viewModel.getPlacesFlow.collectAsState()
        getPlacesFlow.value.let {
            when (it) {
                is Resource.Success -> {
                    ShowSelectedPlaces(viewModel, navController, searchedPlaces)
                }
                is Resource.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    Log.d("Flow", "Entro en Loading")
                    Box(
                        Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    Log.d("Error", "Error al obtener datos")
                }
            }
        }
    }
}

@Composable
private fun ShowSelectedPlaces(
    viewModel: HomeViewModel,
    navController: NavController,
    searchedPlaces: List<Place>?
) {
    if (searchedPlaces != null) {
        ShowAllPlaceSelected(
            viewModel = viewModel,
            navController = navController,
            places = searchedPlaces
        )
    }
}

@Composable
private fun ShowAllPlaceSelected(
    viewModel: HomeViewModel,
    navController: NavController,
    places: List<Place>,
) {
    Spacer(modifier = Modifier.height(5.dp))
    PlaceList(places) {
        viewModel.setSelectedSearchedPlace(it)
        viewModel.cleanImages()
        navController.navigate(AppScreens.SelectedPlace.route) {
            launchSingleTop = true
        }
    }
}

@Composable
private fun PlaceList(
    places: List<Place>,
    onSuggestionSelected: (Place) -> Unit
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        //si no hay sugerencias para revisar se muestra un mensaje, si no se muestran las sugerencias
        if (places.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "No se encontraron datos",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            for (item in places) {
                Place(item) { onSuggestionSelected(item) }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
private fun Place(item: Place, onPlaceClick: () -> Unit) {
    val placeRate = item.placeRate/item.placeNumberOfRaters
    Column() {
        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
                .clickable { onPlaceClick() }

        ) {
            SuggestionPlaceImage(suggestionType = item.placeType)
            Spacer(modifier = Modifier.padding(10.dp))
            Column(Modifier.padding(top = 15.dp, end = 12.dp)) {
                Text(
                    text = item.placeName,
                    fontSize = 18.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.secondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.placeDescription,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.secondary
                )

                StarRate(
                    rate = placeRate.toInt(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalAlignment = Arrangement.End
                )
            }

        }
    }
}
