package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextArea
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import com.google.maps.android.compose.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MakeSuggestion(
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    currentUser: FirebaseUser?
) {

    //Scroll
    var columnScrollingEnabled by remember { mutableStateOf(true) }

    //Ubicación del usuario en live data
    val location by homeViewModel.getLocationLiveData().observeAsState()
    var userLocation = LatLng(0.0, 0.0)
    location?.let {
        userLocation = LatLng(location!!.latitude.toDouble(), location!!.longitude.toDouble())
    }
    //Posicion inicial de la vista del mapa
    val cam = userLocation
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 16f)
    }
    var marker = cam

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScrollingEnabled = true
            Log.d(TAG, "Map camera stopped moving - Enabling column scrolling...")
        }
    }

    // val suggestion  = suggestionViewModel.suggestion.observeAsState().value
    val name: String by suggestionViewModel.name.observeAsState(initial = "")
    val description: String by suggestionViewModel.description.observeAsState(initial = "")

    val suggestionFlow = suggestionViewModel.suggestionFlow.collectAsState()
    val flag = suggestionViewModel.flag.observeAsState()


    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val validateName = suggestionViewModel.validateName.observeAsState()
    val validateDescription = suggestionViewModel.validateDescription.observeAsState()
    val validateNameError = stringResource(R.string.validate_suggestion_name)
    val validateDescriptionError = stringResource(R.string.validate_suggestion_description)

    Box(
        modifier = Modifier
            .padding(16.dp, 50.dp, 16.dp, 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), columnScrollingEnabled)
    ) {
        Column(modifier = Modifier) {
            Text(text = "Realizar Sugerencia", color = MaterialTheme.colors.secondary)
            PlaceNameField(
                name,
                validateName.value,
                validateNameError,
                focusManager
            ) { suggestionViewModel.onFieldsChanged(it, description) }
            Spacer(modifier = Modifier.height(6.dp))
            PlaceDescriptionField(
                description,
                validateDescription.value,
                validateDescriptionError,
                focusManager
            ) { suggestionViewModel.onFieldsChanged(name, it) }
            Spacer(modifier = Modifier.height(6.dp))
            MyPlaceRate()
            Spacer(modifier = Modifier.height(6.dp))
            PlaceSelect(
                homeViewModel,
                suggestionViewModel,
                modifier = Modifier
                    .fillMaxSize()
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
                                        TAG,
                                        "MotionEvent ${it.action} - this never triggers."
                                    )
                                    true
                                }
                            }
                        }
                    ),
                cameraPositionState = cameraPositionState,
                userLocation,
                {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(cam, 16f)
                    suggestionViewModel.setMarker(userLocation)

                },
                {
                    suggestionViewModel.setMarker(it)
                    marker = it
                }
            ) {

            }

            Spacer(modifier = Modifier.height(6.dp))
            Spacer(modifier = Modifier.height(6.dp))
            AddPlaceImages()
            Spacer(modifier = Modifier.height(6.dp))
            SendSuggestionButton {
                sendSuggestionFunction(
                    name,
                    description,
                    marker,
                    currentUser?.email.toString(),
                    suggestionViewModel,
                    context
                )
            }
        }
    }

    suggestionFlow.value.let {
        if (flag.value == true) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, "Sugerencia enviada correctamente", Toast.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    throw IllegalStateException("Error de al enviar sugerencia")
                }
            }
        }


    }

}

@Composable
private fun PlaceNameField(
    placeName: String,
    validatePlaceName: Boolean?,
    validatePlaceNameError: String,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextField(
        value = placeName,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.place_name),
        showError = !validatePlaceName!!,
        errorMessage = validatePlaceNameError,
        leadingIconImageVector = Icons.Default.Place,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
private fun PlaceDescriptionField(
    placeDescription: String,
    validatePlaceDescription: Boolean?,
    validatePlaceDescriptionError: String,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextArea(
        value = placeDescription,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.place_name),
        showError = !validatePlaceDescription!!,
        errorMessage = validatePlaceDescriptionError,
        leadingIconImageVector = Icons.Default.Description,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.clearFocus() }
        )
    )


}

@Composable
private fun MyPlaceRate() {
    Text(text = "hola")
}


@Composable
fun PlaceSelect(
    viewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    userLocation: LatLng,
    onMyPositionButtonClicked: () -> Unit,
    onMapClick: (position: LatLng) -> Unit,
    onMapLoaded: () -> Unit,
) {
    val userMarker = suggestionViewModel.markerLocation.observeAsState()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.background
    ) {
        var isMapLoaded by remember { mutableStateOf(false) }
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(410.dp)
            ) {
                GoogleMap(
                    modifier = modifier
                        .height(350.dp)
                        .fillMaxWidth(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = {
                        suggestionViewModel.setMarker(userLocation)
                        isMapLoaded = true
                        onMapLoaded()
                    },
                    onMapClick = {
                        onMapClick(it)
                    }

                ) {
                    if (userMarker.value != null) {
                        Log.d("Marker", "usermarker no es nulo ${userMarker.value.toString()}")
                        Marker(
                            position = userMarker.value ?: LatLng(0.0, 0.0),
                            title = "Ubicacion de Sugerencia",
                            snippet = "Seleccione la ubicacion exacta",
                        )
                    }

                }
                if (!isMapLoaded) {
                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .fillMaxSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .wrapContentSize()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(6.dp))
            Button(
                onClick = { onMyPositionButtonClicked() }, modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(50.dp)
                    .align(Alignment.End),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    disabledBackgroundColor = Color.DarkGray
                )
            ) {
                Text(
                    stringResource(R.string.my_location),
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Icono de ubicacion",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}


@Composable
fun AddPlaceImages() {
    Text(text = "hola")
}


@Composable
private fun SendSuggestionButton(onSendSelected: () -> Unit) {
    Button(
        onClick = { onSendSelected() }, modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        )
    ) {
        Text(stringResource(R.string.make_suggestion), color = MaterialTheme.colors.onBackground)
    }
}

private fun sendSuggestionFunction(
    name: String,
    description: String,
    marker: LatLng,
    user: String,
    viewModel: MakeSuggestionViewModel,
    context: Context
) {
    if (viewModel.validateDataMakeSuggestion(name, description)) {
        viewModel.makeSuggestion(name, description, marker, user)
        viewModel.cleanSuggestionFields()
    } else {
        Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG).show()
    }
}
