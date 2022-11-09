package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.content.ContentValues
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.StarRate
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ViewSuggestionDetail(viewModel: ReviewSuggestionViewModel) {

    //Scroll
    var columnScrollingEnabled by remember { mutableStateOf(true) }

    val suggestion = viewModel.selectedSuggestion.observeAsState()

    val suggestionPosition = LatLng(
        suggestion.value!!.suggestionLat.toDouble(),
        suggestion.value!!.suggestionLng.toDouble()
    )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(suggestionPosition, 16f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScrollingEnabled = true
            Log.d(ContentValues.TAG, "Map camera stopped moving - Enabling column scrolling...")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp, 16.dp, 0.dp)
            .verticalScroll(rememberScrollState(), columnScrollingEnabled)
    ) {
        SuggestionName(suggestion.value!!.suggestionName)
        SuggestionDesctiption(suggestion.value!!.suggestionDescription)
        SuggestionType(suggestion.value!!.suggestionType)
        SuggestioRate(suggestion.value!!.suggestionRate)
        SuggestionDate(suggestion.value!!.suggestionAddDate)
        SuggestionAddedBy(suggestion.value!!.suggestionAddedBy)
        SuggestionLocation(
            suggestionPosition.latitude,
            suggestionPosition.longitude,
            cameraPositionState,
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
                                    ContentValues.TAG,
                                    "MotionEvent ${it.action} - this never triggers."
                                )
                                true
                            }
                        }
                    }
                )
        )
        SuggestionImages()
        ApproveOrDeclineButtons(
            Modifier,
            { Log.d("Boton", "Rechazado")}
        ) {
            Log.d("Boton", "Aprobado")
        }


        // Text(text = " La sugerencia seleccionada es ${suggestion.value!!.suggestionId}")
    }
}

@Composable
fun ApproveOrDeclineButtons(modifier: Modifier, onDeclineSelected: () -> Unit, onApproveSelected: () -> Unit) {
    Spacer(modifier = Modifier.height(15.dp))
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = { onApproveSelected() }, modifier = Modifier
                .width(180.dp)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                disabledBackgroundColor = Color.DarkGray
            )
        ) {
            Text(
                "Aprobar",
                color = MaterialTheme.colors.onBackground
            )

        }
        Spacer(modifier = Modifier.width(15.dp))
        Button(
            onClick = { onDeclineSelected() }, modifier = Modifier
                .width(180.dp)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant,
                disabledBackgroundColor = Color.DarkGray
            )
        ) {
            Text(
                "Rechazar",
                color = MaterialTheme.colors.onBackground
            )
        }
    }
    Spacer(modifier = Modifier.height(25.dp))
}

@Composable
private fun SuggestionImages() {

}

@Composable
private fun SuggestionLocation(
    suggestionLat: Double,
    suggestionLng: Double,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier
) {
    Box {
        GoogleMap(
            modifier = modifier
                .height(350.dp)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(position = LatLng(suggestionLat, suggestionLng))
        }
    }
}

@Composable
private fun SuggestionAddedBy(suggestionAddedBy: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Usuario sugerente", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = suggestionAddedBy, style = MaterialTheme.typography.h5, fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun SuggestionDate(suggestionAddDate: Date?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Fecha de Sugerencia", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = suggestionAddDate.toString(),
            style = MaterialTheme.typography.h5,
            fontSize = 20.sp
        )
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun SuggestioRate(suggestionRate: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Calificación preliminar del Usuario", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        StarRate(rate = suggestionRate)
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun SuggestionType(suggestionType: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Tipo de Lugar", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = suggestionType, style = MaterialTheme.typography.h5)
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun SuggestionDesctiption(suggestionDescription: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Descripción de Lugar", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = suggestionDescription, style = MaterialTheme.typography.h5)
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun SuggestionName(suggestionName: String) {
    Spacer(modifier = Modifier.height(15.dp))
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Nombre de Lugar", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = suggestionName, style = MaterialTheme.typography.h5)
    }
    Spacer(modifier = Modifier.height(15.dp))
}
