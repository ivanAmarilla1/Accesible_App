package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.usecases.home.MainMap

@Composable
fun ViewSuggestionDetail(viewModel: ReviewSuggestionViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
    ) {
        Text(text = " Detalle de sugerencia")
    }
}