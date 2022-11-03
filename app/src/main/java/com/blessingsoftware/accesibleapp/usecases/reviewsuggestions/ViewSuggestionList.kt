package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ViewSuggestionList(viewModel: ReviewSuggestionViewModel) {
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
        SuggestionList(viewModel, scope)
    }
}

@Composable
fun SuggestionList(viewModel: ReviewSuggestionViewModel, scope: CoroutineScope) {
    //Lugares
    val suggestions by viewModel.suggestions.observeAsState(initial = emptyList())
    Column() {

        for (item in suggestions) {
            Text(item.suggestionName)
            Spacer(modifier = Modifier.height(5.dp))
        }
    }


}
