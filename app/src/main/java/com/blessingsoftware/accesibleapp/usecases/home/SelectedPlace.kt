package com.blessingsoftware.accesibleapp.usecases.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.MySearchedPlace
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun SelectedPlace(
    viewModel: HomeViewModel,
    navController: NavHostController,
    currentUser: FirebaseUser?
) {
    val selectedPlace = viewModel.selectedSearchedPlace.observeAsState()
    val context = LocalContext.current


    //Coroutine Scope
    val scope = rememberCoroutineScope()
    //Para la calificacion
    val showRateDialog = viewModel.showRatingDialog.observeAsState()
    val validatePlaceRate = viewModel.validatePlaceRate.observeAsState()
    val userRating: Int by viewModel.userRating.observeAsState(initial = 0)
    //Para el callback de la calificacion
    val addRateFlow = viewModel.addRateFlow.collectAsState()
    val flag = viewModel.addRateFlag.observeAsState()

    MySearchedPlace(selectedPlace = selectedPlace.value, viewModel = viewModel, context = context, navController) {
        viewModel.setShowDialogTrue()
    }

    RatePlace(
        viewModel,
        rating = userRating,
        validateRate = validatePlaceRate.value,
        showRateDialog.value,
        { viewModel.setUserRating(it) }
    ) {
        scope.launch {
            viewModel.addPlaceRate(selectedPlace.value, it, currentUser)
        }
    }

    addRateFlow.value.let {
        if (flag.value == true) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, "Calificación Enviada", Toast.LENGTH_LONG).show()
                    viewModel.cleanRates()

                }
                is Resource.Failure -> {
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                    viewModel.cleanRates()
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