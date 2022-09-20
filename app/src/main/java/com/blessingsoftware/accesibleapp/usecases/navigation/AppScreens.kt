package com.blessingsoftware.accesibleapp.usecases.navigation

sealed class AppScreens(val route: String) {
    //declaracion de las rutas de las pantallas
    object SplashScreen: AppScreens("splash_screen")
    object LoginView: AppScreens("login_view")
    object HomeView: AppScreens("home_view")
}