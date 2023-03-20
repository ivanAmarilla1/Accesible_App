package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.PlaceTypes
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.*
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseUser
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MakeSuggestion(
    suggestionViewModel: MakeSuggestionViewModel,
    navController: NavController,
    currentUser: FirebaseUser?,
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
        position = CameraPosition.fromLatLngZoom(cam, 12f)
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
        }
    }

    //variables capturadas con LiveData
    val name: String by suggestionViewModel.name.observeAsState(initial = "")
    val description: String by suggestionViewModel.description.observeAsState(initial = "")
    val accessibility: String by suggestionViewModel.accessibility.observeAsState(initial = "")
    val difficulties: String by suggestionViewModel.difficulties.observeAsState(initial = "")

    val userRating: Int by suggestionViewModel.rating.observeAsState(initial = 0)
    val placeType: String by suggestionViewModel.placeType.observeAsState(initial = "Seleccione")
    val suggestionFlow = suggestionViewModel.suggestionFlow.collectAsState()
    val flag = suggestionViewModel.flag.observeAsState()
    //Utils
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //Validaciones
    val validateName = suggestionViewModel.validateName.observeAsState()
    val validateDescription = suggestionViewModel.validateDescription.observeAsState()
    val validateAccessibility = suggestionViewModel.validateAccessibility.observeAsState()
    val validateDifficulties = suggestionViewModel.validateDifficulties.observeAsState()
    val validateType = suggestionViewModel.validateType.observeAsState()
    val validateRate = suggestionViewModel.validateRate.observeAsState()
    val validateNameError = stringResource(R.string.validate_suggestion_name)
    val validateDescriptionError = stringResource(R.string.validate_suggestion_description)
    val validateAccessibilityError = stringResource(R.string.validate_suggestion_accessibility)
    val validateDifficultiesError = stringResource(R.string.validate_suggestion_difficulties)
    val validateTypeError = stringResource(R.string.validate_suggestion_type)
    val validateRateError = stringResource(R.string.validate_suggestion_rate)
    //Dialog
    val showDialog = suggestionViewModel.showDialog.observeAsState()

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
            PlaceAccessibilityChooser(
                validateAccessibility.value,
                validateAccessibilityError
            ) { suggestionViewModel.onChooserChanged(it, difficulties) }
            Spacer(modifier = Modifier.height(20.dp))
            PlaceDifficultiesChooser(
                validateDifficulties.value,
                validateDifficultiesError
            ) {
                suggestionViewModel.onChooserChanged(accessibility, it)
            }
            Spacer(modifier = Modifier.height(20.dp))
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
                context,
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
                    val zoom: Float = if (userLocation == null) 12f else 16f
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(cam, zoom)
                    suggestionViewModel.setMarker(userLocation)

                },
                {
                    suggestionViewModel.setMarker(it)
                    columnScrollingEnabled = true
                }
            ) {

            }
            Spacer(modifier = Modifier.height(15.dp))
            AddPlaceImages(suggestionViewModel)
            SendSuggestionButton {
                sendSuggestionFunction(
                    name,
                    description,
                    accessibility,
                    difficulties,
                    userRating,
                    placeType,
                    userMarker.value,
                    suggestionViewModel,
                    context,
                )
                columnScrollingEnabled = true
            }
        }
        if (currentUser != null) {
            SendSuggestionDialog(
                suggestionViewModel,
                scope,
                name,
                description,
                accessibility,
                difficulties,
                userRating,
                placeType,
                userMarker.value,
                currentUser.uid,
                showDialog.value
            )
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
    //SuggestionBackHandler(suggestionViewModel, navController, scaffoldState, scope)
    CloseDrawerBackHandler(scaffoldState = scaffoldState, scope = scope) {
        BackHandler(enabled = true, onBack = {
            navController.navigate(AppScreens.HomeView.route) {
                popUpTo(AppScreens.HomeView.route) { inclusive = true }
            }
            suggestionViewModel.cleanSuggestionFields()
        })
    }
}

@Composable
private fun SendSuggestionDialog(
    viewModel: MakeSuggestionViewModel,
    scope: CoroutineScope,
    name: String,
    description: String,
    accessibility: String,
    difficulties: String,
    rate: Int,
    placeType: String,
    marker: LatLng?,
    user: String,
    showDialog: Boolean?,
) {
    val buttonText = "Enviar"
    val title = "Enviar Sugerencia"
    val text =
        "La sugerencia será evaluada por un administrador antes de incluir el lugar en la aplicación"
    val image = painterResource(id = R.drawable.send)
    if (showDialog == true && marker != null) {
        CustomDialog(
            buttonText = buttonText,
            tittle = title,
            text = text,
            image = image,
            onDismissRequest = { viewModel.setShowDialogFalse() }) {
            scope.launch {
                viewModel.makeSuggestion(name, description, accessibility, difficulties, rate, placeType, marker, user)
            }
        }
    }
}

@Composable
private fun PlaceType(
    placeType: String,
    validateType: Boolean?,
    validateTypeError: String,
    onPlaceTypeSelected: (String) -> Unit
) {
    val typeList = PlaceTypes.values()

    val list: MutableList<String> = arrayListOf()
    for (item in typeList) {
        list.add(item.description())
    }


    Text(text = "Tipo de Lugar", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    DropDownMenu(
        placeType,
        list,
        !validateType!!,
        validateTypeError,
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
    ) {
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
    Text(text = "Nombre del Lugar", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
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
    Text(text = "Descripción del Lugar", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
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
            imeAction = ImeAction.Default
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
        )
    )
}

@Composable
private fun PlaceAccessibilityChooser(
    validateAccessibility: Boolean?,
    validateAccessibilityError: String,
    onChooserChanged: (String) -> Unit
) {
    var checkedList = ""
    val accessibility = listOf(
        "Rampas para sillas de ruedas en accesos",
        "Barandales de apoyo",
        "huellas podotáctiles",
        "Baño para discapacitados",
        "Ascensores", "Estacioniento para discapacitados"
    )
    Spacer(modifier = Modifier.height(5.dp))
    val options = accessibility.map {
        val checked = remember { mutableStateOf(false) }
        Option(
            checked = checked.value,
            onCheckedChange = { checked.value = it },
            label = it,
        )
    }
    val x = CheckboxList(
        options = options,
        listTitle = "Accesibilidades del Lugar",
        validateAccessibility,
        validateAccessibilityError
    )
    checkedList = ""
    x.forEach {
        checkedList += "- $it\r"
    }
    onChooserChanged(checkedList)
}

@Composable
private fun PlaceDifficultiesChooser(
    validateDifficulties: Boolean?,
    validateDifficultiesError: String,
    onChooserChanged: (String) -> Unit
) {
    var checkedList = ""
    val difficulties = listOf(
        "Escaleras sin rampas y/o ascensores",
        "Espacio Reducido",
        "Sin estacionamiento para discapacitados",
        "Ninguno"
    )
    Spacer(modifier = Modifier.height(5.dp))
    val options = difficulties.map {
        val checked = remember { mutableStateOf(false) }
        Option(
            checked = checked.value,
            onCheckedChange = { checked.value = it },
            label = it,
        )
    }
    val x = CheckboxList(
        options = options,
        listTitle = "Potenciales dificultades de accesibilidad",
        validateDifficulties,
        validateDifficultiesError
    )
    checkedList = ""
    x.forEach {
        checkedList += "- $it\r"
    }
    onChooserChanged(checkedList)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MyPlaceRate(
    rating: Int,
    validateRate: Boolean?,
    validateRateError: String,
    onRatingChange: (Int) -> Unit
) {
    Text(text = "Agregue su calificación personal", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
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
private fun PlaceSelect(
    suggestionViewModel: MakeSuggestionViewModel,
    userMarker: LatLng?,
    context: Context,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    userLocation: LatLng?,
    onMyPositionButtonClicked: () -> Unit,
    onMapClick: (position: LatLng) -> Unit,
    onMapLoaded: () -> Unit,
) {
    //propiedades y UI del mapa
    val mapSettings = if (isSystemInDarkTheme()) R.raw.mapwithpoidarksettings else R.raw.mapwithpoilightsettings
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


    Text(text = "Indique la ubicación del lugar en el mapa", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(10.dp))
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
                    properties = properties,
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = {
                        suggestionViewModel.setMarker(userLocation)
                        isMapLoaded = true
                        onMapLoaded()
                    },
                    onMapClick = {
                        onMapClick(it)
                    },
                    onPOIClick = {
                        onMapClick(it.latLng)
                    }

                ) {
                    if (userMarker != null) {
                        //Log.d("Marker", "usermarker no es nulo $userMarker")
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
                    text = if (userLocation != null) stringResource(R.string.my_location) else "Reiniciar",
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
private fun AddPlaceImages(suggestionViewModel: MakeSuggestionViewModel) {
    Text(text = "Agregue imágenes del lugar", color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
    SuggestionImages(viewModel = suggestionViewModel)
    Spacer(modifier = Modifier.height(10.dp))
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
    accessibility: String,
    difficulties: String,
    rate: Int,
    placeType: String,
    userMarker: LatLng?,
    viewModel: MakeSuggestionViewModel,
    context: Context,
) {
    if (userMarker != null) {
        if (viewModel.validateDataMakeSuggestion(
                name,
                description,
                placeType,
                rate,
                accessibility,
                difficulties
            )
        ) {
            if (viewModel.isImageByteArrayEmpty()) {
                Toast.makeText(
                    context,
                    "Selecciones al menos una imagen del lugar",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.setShowDialogTrue()
            }
        } else {
            Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG)
                .show()
        }
    } else {
        Toast.makeText(context, "Seleccione una ubicación en el mapa", Toast.LENGTH_LONG).show()
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
