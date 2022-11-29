package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.ui.composables.DropDownMenu
import com.blessingsoftware.accesibleapp.ui.composables.StarRate
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun ViewSuggestionList(viewModel: ReviewSuggestionViewModel, navController: NavController) {

    //Sugerencias
    val suggestions by viewModel.suggestions.observeAsState(initial = emptyList())
    //El tipo de lista de sugerencias a ser mostrada, si son las pendientes, aprobadas o rechazadas
    val suggestionViewType = viewModel.suggestionViewType.observeAsState(initial = 1)
    //Corrutina que hace la consulta para obtener las sugerencias
    LaunchedEffect(key1 = suggestionViewType.value) {//Cada vez que cambia la key se ejecuta el launched effect
        viewModel.getSuggestions(suggestionViewType.value)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 50.dp, 0.dp, 0.dp)
    ) {
        SelectSuggestionType(suggestionViewType.value) {
            viewModel.setSuggestionViewType(it)
        }

        val getSuggestionFlow = viewModel.getSuggestionFlow.collectAsState()
        getSuggestionFlow.value.let {
            Log.d("Valor", "Entrando en flow")
            when (it) {
                is Resource.Success -> {
                    ShowAllSuggestions(viewModel, navController, suggestions)
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
                    throw IllegalStateException("Error al obtener Sugerencias")
                }
            }
        }
    }
}

@Composable
private fun ShowAllSuggestions(
    viewModel: ReviewSuggestionViewModel,
    navController: NavController,
    suggestions: List<Suggestion>,
) {
    Spacer(modifier = Modifier.height(5.dp))
    SuggestionList(suggestions) {
        viewModel.setSelectedSuggestion(it)
        viewModel.cleanImages()
        navController.navigate(AppScreens.SuggestionDetail.route) {
            launchSingleTop = true
        }
    }
}

@Composable
fun SuggestionList(
    suggestions: List<Suggestion>,
    onSuggestionSelected: (Suggestion) -> Unit
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        //si no hay sugerencias para revisar se muestra un mensaje, si no se muestran las sugerencias
        if (suggestions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "No hay sugerencias",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            for (item in suggestions) {
                Suggestion(item) { onSuggestionSelected(item) }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun SelectSuggestionType(selectedItem: Int, onSuggestionTypeSelected: (Int) -> Unit) {
    val typeList = listOf(
        "Pendientes",
        "Aprobados",
        "Rechazados",
    )
    DropDownMenu(
        typeList[selectedItem - 1], typeList, false, "",
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Transparent)
    ) {
        val x: Int = when (it) {
            "Pendientes" -> 1
            "Aprobados" -> 2
            "Rechazados" -> 3
            else -> 1
        }
        onSuggestionTypeSelected(x)
    }
}

@Composable
private fun Suggestion(item: Suggestion, onSuggestionClick: () -> Unit) {
    Column() {
        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(10.dp))
                .clickable { onSuggestionClick() }

        ) {
            SuggestionImage(item.suggestionType)
            Spacer(modifier = Modifier.padding(10.dp))
            Column(Modifier.padding(top = 15.dp, end = 12.dp)) {
                Text(
                    text = item.suggestionName,
                    fontSize = 18.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.secondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.suggestionDescription,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.secondary
                )
                PreliminaryRate(item.suggestionRate)
            }

        }
    }
}

@Composable
private fun PreliminaryRate(suggestionRate: Int) {
    StarRate(
        rate = suggestionRate,
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalAlignment = Arrangement.End
    )
}

@Composable
fun SuggestionImage(suggestionType: String) {
    var img = Icons.Filled.LocalParking
    when (suggestionType) {
        "Estacionamiento" -> img = Icons.Filled.WheelchairPickup
        "Comercio" -> img = Icons.Filled.Store
        "Lugar Público" -> img = Icons.Filled.Apartment
        "Entidad Estatal" -> img = Icons.Filled.AccountBalance
        "Restaurante" -> img = Icons.Filled.Restaurant
        "Hotel" -> img = Icons.Filled.Hotel
        "Punto de Interés" -> img = Icons.Filled.Interests
        "Zona de Entretenimiento" -> img = Icons.Filled.Nightlife
        "Otros" -> img = Icons.Filled.Business

    }
    Box(modifier = Modifier.padding(start = 10.dp, top = 25.dp)) {
        Icon(
            img,
            contentDescription = "Suggestion type",
            tint = MaterialTheme.colors.secondary,
            modifier = Modifier//modificadores de tamaño, forma y fondo
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.onSecondary)
                .padding(15.dp),
        )
    }
}

