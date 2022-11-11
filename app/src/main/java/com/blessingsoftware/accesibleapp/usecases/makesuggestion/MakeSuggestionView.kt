package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.*
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MakeSuggestion(
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    navController: NavController,
    authViewModel: AuthViewModel,
    scaffoldState: ScaffoldState
) {
    //Scroll
    var columnScrollingEnabled by remember { mutableStateOf(true) }
    //Ubicación del usuario en live data
    val location by suggestionViewModel.getLocationLiveData().observeAsState()
    var userLocation: LatLng? = null
    location?.let {
        userLocation = LatLng(location!!.latitude.toDouble(), location!!.longitude.toDouble())
    }
    //Marker
    val userMarker = suggestionViewModel.markerLocation.observeAsState()
    if (userMarker.value == null) {
        suggestionViewModel.setInitialMarker(userLocation)
    }
    //Posicion inicial de la vista del mapa
    var cam = LatLng(-25.28269856303251, -57.60271740849931)
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 10f)
    }
    userLocation?.let {
        cam = userLocation as LatLng
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(cam, 16f)
        }
    }

    //Captura el movimiento del mapa, si el mapa se mueve desactiva el verticalScroll, si no, lo activa
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScrollingEnabled = true
            Log.d(TAG, "Map camera stopped moving - Enabling column scrolling...")
        }
    }
    //variables capturadas con LiveData
    val name: String by suggestionViewModel.name.observeAsState(initial = "")
    val description: String by suggestionViewModel.description.observeAsState(initial = "")
    val userRating: Int by suggestionViewModel.rating.observeAsState(initial = 0)
    val placeType: String by suggestionViewModel.placeType.observeAsState(initial = "Seleccione")
    val suggestionFlow = suggestionViewModel.suggestionFlow.collectAsState()
    val isGPSOn = suggestionViewModel.isGPSOn.observeAsState()
    val flag = suggestionViewModel.flag.observeAsState()
    //Utils
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //Validaciones
    val validateName = suggestionViewModel.validateName.observeAsState()
    val validateDescription = suggestionViewModel.validateDescription.observeAsState()
    val validateType = suggestionViewModel.validateType.observeAsState()
    val validateRate = suggestionViewModel.validateRate.observeAsState()
    val validateNameError = stringResource(R.string.validate_suggestion_name)
    val validateDescriptionError = stringResource(R.string.validate_suggestion_description)
    val validateTypeError = stringResource(R.string.validate_suggestion_type)
    val validateRateError = stringResource(R.string.validate_suggestion_rate)

    Box(
        modifier = Modifier
            .padding(16.dp, 50.dp, 16.dp, 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), columnScrollingEnabled)
    ) {
        Column(modifier = Modifier) {
            Spacer(modifier = Modifier.height(15.dp))
            PlaceNameField(
                name,
                validateName.value,
                validateNameError,
                focusManager
            ) { suggestionViewModel.onFieldsChanged(it, description) }
            Spacer(modifier = Modifier.height(15.dp))
            PlaceDescriptionField(
                description,
                validateDescription.value,
                validateDescriptionError,
                focusManager
            ) { suggestionViewModel.onFieldsChanged(name, it) }
            Spacer(modifier = Modifier.height(15.dp))
            PlaceType(placeType, validateType.value, validateTypeError) {
                suggestionViewModel.setPlaceType(it)
            }
            Spacer(modifier = Modifier.height(20.dp))
            MyPlaceRate(userRating, validateRate.value, validateRateError) {
                suggestionViewModel.setRating(it)
            }
            Spacer(modifier = Modifier.height(15.dp))
            PlaceSelect(
                suggestionViewModel,
                userMarker.value,
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
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(cam, 10f)
                    suggestionViewModel.setMarker(userLocation)

                },
                {
                    suggestionViewModel.setMarker(it)
                    columnScrollingEnabled = true
                }
            ) {

            }
            Spacer(modifier = Modifier.height(15.dp))
            //AddPlaceImages()
            //Spacer(modifier = Modifier.height(6.dp))
            SendSuggestionButton {
                sendSuggestionFunction(
                    name,
                    description,
                    userRating,
                    placeType,
                    userMarker.value!!,
                    authViewModel.currentUser?.email.toString(),
                    suggestionViewModel,
                    context,
                    scope
                )
                columnScrollingEnabled = true
            }
        }
    }

    suggestionFlow.value.let {
        if (flag.value == true) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, "Sugerencia enviada correctamente", Toast.LENGTH_LONG)
                        .show()
                    suggestionViewModel.cleanSuggestionFields()
                }
                is Resource.Failure -> {
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                    suggestionViewModel.cleanSuggestionFields()
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
    SuggestionBackHandler(suggestionViewModel, navController, scaffoldState, scope)
}

@Composable
fun PlaceType(
    placeType: String,
    validateType: Boolean?,
    validateTypeError: String,
    onPlaceTypeSelected: (String) -> Unit
) {
    val typeList = listOf(
        "Estacionamiento",
        "Comercio",
        "Lugar Público",
        "Entidad Estatal",
        "Restaurante",
        "Hotel",
        "Punto de Interés",
        "Zona de Entretenimiento",
        "Otros"
    )

    Text(text = "Tipo de Lugar", color = MaterialTheme.colors.secondary)
    Spacer(modifier = Modifier.height(5.dp))
    DropDownMenu(placeType, typeList, !validateType!!, validateTypeError, Modifier.fillMaxWidth()) {
        onPlaceTypeSelected(it)
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
    Text(text = "Nombre del Lugar", color = MaterialTheme.colors.secondary)
    Spacer(modifier = Modifier.height(5.dp))
    CustomOutlinedTextFieldTwo(
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
    Text(text = "Descripción del Lugar", color = MaterialTheme.colors.secondary)
    Spacer(modifier = Modifier.height(5.dp))
    CustomOutlinedTextArea(
        value = placeDescription,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.place_description),
        showError = !validatePlaceDescription!!,
        errorMessage = validatePlaceDescriptionError,
        leadingIconImageVector = Icons.Default.Description,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
        )
    )


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MyPlaceRate(
    rating: Int,
    validateRate: Boolean?,
    validateRateError: String,
    onRatingChange: (Int) -> Unit
) {
    Text(text = "Agregue su calificación personal", color = MaterialTheme.colors.secondary)
    Spacer(modifier = Modifier.height(5.dp))
    Column() {
        RatingBar(
            rating = rating,
            validateRate = !validateRate!!,
            validateRateError = validateRateError
        ) {
            onRatingChange(it)
        }
    }

}


@Composable
fun PlaceSelect(
    suggestionViewModel: MakeSuggestionViewModel,
    userMarker: LatLng?,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    userLocation: LatLng?,
    onMyPositionButtonClicked: () -> Unit,
    onMapClick: (position: LatLng) -> Unit,
    onMapLoaded: () -> Unit,
) {
    Text(text = "Indique la ubicación del lugar en el mapa", color = MaterialTheme.colors.secondary)
    Spacer(modifier = Modifier.height(5.dp))
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
                    if (userMarker != null) {
                        Log.d("Marker", "usermarker no es nulo $userMarker")
                        Marker(
                            position = userMarker,
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
                    text = if (userLocation != null) stringResource(R.string.my_location) else "Reiniciar cámara",
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
    rate: Int,
    placeType: String,
    marker: LatLng,
    user: String,
    viewModel: MakeSuggestionViewModel,
    context: Context,
    scope: CoroutineScope
) {

    if (viewModel.validateDataMakeSuggestion(name, description, placeType, rate)) {
        scope.launch {
            viewModel.makeSuggestion(name, description, rate, placeType, marker, user)
        }
    } else {
        Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun SuggestionBackHandler(
    suggestionViewModel: MakeSuggestionViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    if (scaffoldState.drawerState.isOpen) {
        BackHandler(enabled = true, onBack = {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        })
    } else {
        BackHandler(enabled = true, onBack = {
            navController.navigate(AppScreens.HomeView.route) {
                popUpTo(AppScreens.HomeView.route) { inclusive = true }
            }
            suggestionViewModel.cleanSuggestionFields()
        })
    }
}
