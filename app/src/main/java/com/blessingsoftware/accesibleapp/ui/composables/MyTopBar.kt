package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyTopBar(navController: NavController, tittle: String) {
    TopAppBar(
        modifier = Modifier.height(50.dp),
        elevation = 0.dp,
        title = {
            ReusableTittle(tittle, textColor = MaterialTheme.colors.secondary, maxLines = 1, textOverflow = TextOverflow.Ellipsis,)
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Arrow Back",
                    tint = MaterialTheme.colors.secondary
                )
            }
        },
        backgroundColor = Color.Transparent
    )
}

