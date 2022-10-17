package com.blessingsoftware.accesibleapp.usecases.home

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AUTH_ROUTE
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker


@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {
    /*rememberSystemUiController().apply {
        setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = MaterialTheme.colors.isLight
        )
    }*/
    GetUserLocation(viewModel)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
    ) {
        MainMap(viewModel)
    }


    //HomeBackHandler()
}

@Composable
fun MainMap(viewModel: HomeViewModel) {

    val userLat = viewModel.userCurrentLat.observeAsState()
    val userLng = viewModel.userCurrentLng.observeAsState()
    val userLocation = LatLng(userLat.value!!, userLng.value!!)
    Log.d("pickup: ", userLocation.toString() )
    GoogleMap(modifier = Modifier.fillMaxSize()) {
        Marker(position = userLocation, title = "Tu ubicación", snippet = "Ubicación en tiempo real")

    }
}

//Obtener ubicacion de usuario
@Composable
private fun GetUserLocation(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        if (viewModel.locationPermissionGranted.value == true) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastKnownLocation = task.result

                    if (lastKnownLocation != null) {
                        viewModel.currentUserGeoCoord(
                            LatLng(
                                lastKnownLocation.latitude,
                                lastKnownLocation.longitude
                            )
                        )
                    }
                } else {
                    Log.d("Exception", " Current User location is null")
                }
            }

        }
    } catch (e: SecurityException) {
        Log.d("Exception", "Exception:  $e.message.toString()")
    }
}
@Composable
private fun HomeBackHandler() {
    val activity = (LocalContext.current as? Activity)
    androidx.activity.compose.BackHandler(enabled = true, onBack = {
        activity?.finish()

        Log.d("BackHandler", "Boton atras")
    })
}

@Composable
fun Home(modifier: Modifier, viewModel: AuthViewModel?, navController: NavController) {
    Column(modifier = modifier) {
        Text("Home Screen", color = MaterialTheme.colors.secondary)
        viewModel?.currentUser?.displayName?.let {
            Text(
                it.trim(),
                color = MaterialTheme.colors.secondary
            )
        }
        viewModel?.currentUser?.email?.let {
            Text(
                it.trim(),
                color = MaterialTheme.colors.secondary
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        LogOutButton(viewModel, navController)
    }

}

@Composable
fun LogOutButton(viewModel: AuthViewModel?, navController: NavController) {
    val context = LocalContext.current
    Button(
        onClick = {
            viewModel?.logOut(context)
            navController.navigate(AUTH_ROUTE) {
                popUpTo(AppScreens.HomeView.route) { inclusive = true }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        ),
    ) {
        Text(stringResource(R.string.logout), color = MaterialTheme.colors.onBackground)
    }
}

@Preview(showBackground = true, name = "Light mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun HomeScreenPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            //HomeView(null, rememberNavController())
        }
    }
}