package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@Composable
fun DefaultMapMarker(
    suggestionLat: Double,
    suggestionLng: Double,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapSettings =
        if (isSystemInDarkTheme()) R.raw.nightmapsettings else R.raw.standardmapsettings
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapSettings)
            )
        )
    }

    Text(
        text = "Ubicación del lugar",
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.secondary,
        modifier = modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    Box {
        GoogleMap(
            modifier = modifier
                .height(350.dp)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = properties,
        ) {
            Marker(position = LatLng(suggestionLat, suggestionLng))
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}