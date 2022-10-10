package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

const val ROOT_ROUTE = "root"
const val AUTH_ROUTE = "auth"
const val HOME_ROUTE = "home"


sealed class AppScreens(val route: String, val tittle: String, val icon: ImageVector,val showBottomBar: Boolean) {
    //declaracion de las rutas de las pantallas

    //Splash
    object SplashScreen: AppScreens("splash_screen", "Splash Screen", Icons.Filled.Desk, false)

    //Auth rutes
    object LoginView: AppScreens("login_view","Iniciar Sesión", Icons.Filled.PermIdentity,false)
    object SignUpView: AppScreens("signup_view","Home", Icons.Filled.Home,false)

    //Home routes
    object HomeView: AppScreens("home_view","Home", Icons.Filled.AccountCircle,true)
    object RandomView: AppScreens("random_view","Random", Icons.Filled.Search, true)
}