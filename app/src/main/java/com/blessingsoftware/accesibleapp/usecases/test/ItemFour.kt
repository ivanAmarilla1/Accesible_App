package com.blessingsoftware.accesibleapp.usecases.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ItemFour(navController: NavController) {
    Box(modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 50.dp)) {
        Column(modifier = Modifier) {
            Text("4st Screen", color = MaterialTheme.colors.secondary)
        }
    }
}