package com.blessingsoftware.accesibleapp.usecases.splashscreen

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun SplashScreen(authViewModel: AuthViewModel?, navController: NavController) {
    var loginFlag = authViewModel?.flag?.observeAsState()
    val loginFlow = authViewModel?.loginFlow?.collectAsState()
    // Corrutina para confirmar si hay una sesion activa de usuario

    //Log.d("loginflow", "Ingresando a loginflow")
    LaunchedEffect(Unit) {
        if (loginFlow?.value != null) {
            when (loginFlow.value) {
                is Resource.Success -> {
                    navController.navigate(AppScreens.HomeView.route) {
                        popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(AppScreens.LoginView.route) {
                        popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        } else {
            navController.navigate(AppScreens.LoginView.route) {
                popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
            }
        }
    }
    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
        Text(
            "Bienvenido",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}