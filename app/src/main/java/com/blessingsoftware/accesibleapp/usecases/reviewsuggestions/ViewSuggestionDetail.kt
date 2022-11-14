package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.content.ContentValues
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.ui.composables.CustomDialog
import com.blessingsoftware.accesibleapp.ui.composables.StarRate
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ViewSuggestionDetail(viewModel: ReviewSuggestionViewModel) {
    ShowSuggestionDetails(viewModel = viewModel)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ShowSuggestionDetails(viewModel: ReviewSuggestionViewModel) {
    //Coroutine Scope
    val scope = rememberCoroutineScope()
    //Para el callback
    val aproveSuggestionFlow = viewModel.approveSuggestionFlow.collectAsState()
    val flag = viewModel.approveSuggestionFlag.observeAsState()
    //Para el dialog
    val showDialog = viewModel.showDialog.observeAsState()
    var approveOrDeclineDialog by rememberSaveable { mutableStateOf(false) }
    //Utils
    val context = LocalContext.current
    //Scroll
    var columnScrollingEnabled by remember { mutableStateOf(true) }
    //sugerencia seleccionada
    val suggestion = viewModel.selectedSuggestion.observeAsState()
    //posicion para el mapa
    val suggestionPosition = LatLng(
        suggestion.value!!.suggestionLat.toDouble(),
        suggestion.value!!.suggestionLng.toDouble()
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
    //Mensaje
    val message = viewModel.message.observeAsState(initial = "")

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
        SuggestionStatus(suggestion.value!!.suggestionApproveStatus)
        if (suggestion.value!!.suggestionApproveStatus == 1) {
            ApproveOrDeclineButtons(
                Modifier,
                {
                    scope.launch {
                        //Cuando se da click en rechazar
                        approveOrDeclineDialog = false
                        viewModel.setShowDialogTrue()

                    }
                }
            ) {
                scope.launch {
                    //Cuando se da click en Aprobar
                    approveOrDeclineDialog = true
                    viewModel.setShowDialogTrue()
                    //viewModel.approveSuggestion(suggestion.value!!)
                }
            }
        }


        SuggestionDialog(viewModel, scope, showDialog.value!!, approveOrDeclineDialog, suggestion)

    }
    aproveSuggestionFlow.value.let {
        if (flag.value == true) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, message.value, Toast.LENGTH_LONG).show()
                    viewModel.clean()
                }
                is Resource.Failure -> {
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                    viewModel.clean()
                }
                Resource.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    throw IllegalStateException("Error de al procesar sugerencia")
                }
            }
        }
    }


}


@Composable
fun SuggestionDialog(
    viewModel: ReviewSuggestionViewModel,
    scope: CoroutineScope,
    showDialog: Boolean?,
    approveOrDecline: Boolean,
    suggestion: State<Suggestion?>
) {
    val buttonText: String
    val title: String
    val text: String
    val image: Painter

    if (approveOrDecline) {//si es true es aprobar
        buttonText = "Aprobar"
        title = "Aprobar Sugerencia"
        text = "Esta seguro de que desea aprobar esta sugerencia?"
        image = painterResource(id = R.drawable.checked)
    } else {//si es falso es rechazar
        buttonText = "Rechazar"
        title = "Rechazar Sugerencia"
        text = "Esta seguro de que desea rechazar esta sugerencia?"
        image = painterResource(id = R.drawable.close)
    }


    if (showDialog == true) {
        CustomDialog(
            buttonText = buttonText,
            tittle = title,
            text = text,
            image = image,
            onDismissRequest = { viewModel.setShowDialogFalse() })
        {
            if (approveOrDecline) {//Si es true es aprobar
                scope.launch {
                    viewModel.approveSuggestion(suggestion.value!!)
                }
            } else {//si es false es rechazar
                scope.launch {
                    viewModel.declineSuggestion(suggestion.value!!)
                }
            }
        }
    }
}

@Composable
fun ApproveOrDeclineButtons(
    modifier: Modifier,
    onDeclineSelected: () -> Unit,
    onApproveSelected: () -> Unit
) {
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
fun SuggestionStatus(suggestionStatus: Int) {

    val status = when (suggestionStatus) {
        1 -> "Pendiente"
        2 -> "Aprobado"
        else -> "Rechazado"
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    ) {
        Text(text = "Estado de la sugerencia", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = status, style = MaterialTheme.typography.h5, fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(15.dp))
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
    Spacer(modifier = Modifier.height(15.dp))
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


/*
* AlertDialog(
            onDismissRequest = { viewModel.setShowDialogFalse() },
            modifier = Modifier.background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp)),
            confirmButton = {
                TextButton(onClick = {
                    if (approveOrDecline) {
                        scope.launch {
                            viewModel.approveSuggestion(suggestion.value!!)
                        }
                    } else {
                        scope.launch {
                            viewModel.declineSuggestion(suggestion.value!!)
                        }
                    }
                }) {
                    Text(text = buttonText)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDialogFalse() }) {
                    Text(text = "Cancelar")
                }
            },
            title = { Text(text = title) },
            text = { Text(text = text) })*/