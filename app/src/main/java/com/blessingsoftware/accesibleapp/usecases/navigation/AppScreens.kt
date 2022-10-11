package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

const val ROOT_ROUTE = "root"
const val AUTH_ROUTE = "auth"
const val HOME_ROUTE = "home"


sealed class AppScreens(
    val route: String, val tittle: String, val icon_outlined: ImageVector?, val icon_filled: ImageVector?) {
    //declaracion de las rutas de las pantallas

    //Splash
    object SplashScreen : AppScreens("splash_screen", "Splash Screen", null, null)

    //Auth rutes
    object LoginView : AppScreens("login_view", "Iniciar Sesión",null, null)
    object SignUpView : AppScreens("signup_view", "Home", null, null)

    //Home routes
    object HomeView : AppScreens("home_view", "Home", Icons.Outlined.Home, Icons.Filled.Home )
    object RandomView : AppScreens("random_view", "Random", Icons.Outlined.Search, Icons.Filled.Search)
}