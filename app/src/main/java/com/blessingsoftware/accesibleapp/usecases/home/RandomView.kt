package com.blessingsoftware.accesibleapp.usecases.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel

@Composable
fun RandomView(navController: NavController) {
    Column(modifier = Modifier) {
        Text("Random Screen", color = MaterialTheme.colors.secondary)

    }
}