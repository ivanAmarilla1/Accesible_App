package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SuggestionPlaceImage(suggestionType: String) {
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