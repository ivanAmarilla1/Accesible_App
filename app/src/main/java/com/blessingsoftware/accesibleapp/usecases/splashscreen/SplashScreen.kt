package com.blessingsoftware.accesibleapp.usecases.splashscreen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AUTH_ROUTE
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE

@Composable
fun SplashScreen(authViewModel: AuthViewModel?, navController: NavController) {
    var loginFlag = authViewModel?.flag?.observeAsState()
    val loginFlow = authViewModel?.loginFlow?.collectAsState()

    // Corrutina para confirmar si hay una sesion activa de usuario
    LaunchedEffect(Unit) {
        Log.d("Loginflow value",loginFlow?.value.toString())
        if (loginFlow?.value != null) {
            when (loginFlow.value) {
                is Resource.Success -> {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(AUTH_ROUTE) {
                        popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        } else {
            navController.navigate(AUTH_ROUTE) {
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

@Preview(showBackground = true, name = "Light mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SplashScreenPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            SplashScreen(null, rememberNavController())
        }
    }
}