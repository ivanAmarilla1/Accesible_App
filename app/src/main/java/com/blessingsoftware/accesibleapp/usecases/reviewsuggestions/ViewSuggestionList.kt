package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.ui.composables.StarRate
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ViewSuggestionList(viewModel: ReviewSuggestionViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.getSuggestions()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 50.dp, 0.dp, 0.dp)
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        SuggestionList(viewModel) {
            viewModel.setSelectedSuggestion(it)
            navController.navigate(AppScreens.SuggestionDetail.route) {
                launchSingleTop = true
            }
        }
    }
}


/*
**/

@Composable
fun SuggestionList(
    viewModel: ReviewSuggestionViewModel,
    onSuggestionSelected: (Suggestion) -> Unit
) {
    //Lugares
    val suggestions by viewModel.suggestions.observeAsState(initial = emptyList())
    Column(Modifier.verticalScroll(rememberScrollState())) {
        for (item in suggestions) {
            Suggestion(item) { onSuggestionSelected(item) }
            Spacer(modifier = Modifier.height(5.dp))
        }
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

