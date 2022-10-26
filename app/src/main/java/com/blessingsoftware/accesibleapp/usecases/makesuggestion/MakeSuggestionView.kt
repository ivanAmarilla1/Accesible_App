package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextArea
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MakeSuggestion(homeViewModel: HomeViewModel, suggestionViewModel: MakeSuggestionViewModel) {

    //Scroll
    val scrollState = rememberScrollState()
    val scrollEnabled = suggestionViewModel.scrollEnabled.observeAsState()

    // val suggestion  = suggestionViewModel.suggestion.observeAsState().value
    val name: String by suggestionViewModel.name.observeAsState(initial = "")
    val description: String by suggestionViewModel.description.observeAsState(initial = "")

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val validateName = suggestionViewModel.validateName.observeAsState()
    val validateDescription = suggestionViewModel.validateDescription.observeAsState()
    val validateNameError = stringResource(R.string.validate_suggestion_name)
    val validateDescriptionError = stringResource(R.string.validate_suggestion_description)

    Box(
        modifier = Modifier
            .padding(16.dp, 50.dp, 16.dp, 16.dp)
            .verticalScroll(scrollState)
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
            PlaceSelect(homeViewModel, suggestionViewModel)
            Spacer(modifier = Modifier.height(6.dp))
            AddPlaceImages()
            Spacer(modifier = Modifier.height(6.dp))
            SendSuggestionButton {
                sendSuggestionFunction(
                    name,
                    description,
                    suggestionViewModel,
                    context
                )
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
private fun PlaceSelect(viewModel: HomeViewModel, suggestionViewModel: MakeSuggestionViewModel) {
    //Ubicación del usuario en live data
    val location by viewModel.getLocationLiveData().observeAsState()
    var userLocation = LatLng(0.0, 0.0)
    location?.let {
        userLocation = LatLng(location!!.latitude.toDouble(), location!!.longitude.toDouble())
    }

    //Posicion inicial de la vista del mapa
    val cam = userLocation
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cam, 15f)
    }
    Box(
        modifier = Modifier
            .height(410.dp)
            .fillMaxWidth()
    ) {
        Column() {
            GoogleMap(
                onMapLongClick = {  },
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth(), cameraPositionState = cameraPosition


            ) {

                if (userLocation != LatLng(0.0, 0.0)) {
                    Marker(
                        position = userLocation,
                        title = "Tu ubicación",
                        snippet = "Ubicación en tiempo real",
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { }, modifier = Modifier
                    .fillMaxWidth(0.4f)
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
    viewModel: MakeSuggestionViewModel,
    context: Context
) {
    if (viewModel.validateDataMakeSuggestion(name, description)) {
        viewModel.makeSuggestion(name, description)
    } else {
        Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG).show()
    }
}
