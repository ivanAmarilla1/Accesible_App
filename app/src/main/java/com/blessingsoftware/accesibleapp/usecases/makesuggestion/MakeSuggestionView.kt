package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MakeSuggestion(homeViewModel: HomeViewModel) {

    val placeName = "Nombre Lugar"

    val focusManager = LocalFocusManager.current


    Box(modifier = Modifier.padding(16.dp, 50.dp, 16.dp, 16.dp)) {
        Column(modifier = Modifier) {
            Text(text = "Realizar Sugerencia", color = MaterialTheme.colors.secondary)
            PlaceSelect(homeViewModel)
            Spacer(modifier = Modifier.height(6.dp))
            PlaceNameField(placeName, true, "error de nombre", focusManager) { }
            Spacer(modifier = Modifier.height(6.dp))
            PlaceDescriptionField()
            Spacer(modifier = Modifier.height(6.dp))
            MyPlaceRate()
            Spacer(modifier = Modifier.height(6.dp))
            AddPlaceImages()
            Spacer(modifier = Modifier.height(6.dp))
            SendSuggestionButton()
        }
    }

}

@Composable
private fun PlaceSelect(viewModel: HomeViewModel) {
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
        leadingIconImageVector = Icons.Default.AlternateEmail,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
private fun PlaceDescriptionField() {
    Text(text = "Place Description")
}

@Composable
private fun MyPlaceRate() {
    Text(text = "hola")
}

@Composable
fun AddPlaceImages() {
    Text(text = "hola")
}

@Composable
fun SendSuggestionButton() {
    Text(text = "hola")
}
