package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.usecases.home.HomeView
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.authentication.LoginView
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.authentication.SignUpView
import com.blessingsoftware.accesibleapp.usecases.splashscreen.SplashScreen

@Composable
fun AppNavigation(authViewModel: AuthViewModel, homeViewModel: HomeViewModel) {
    val navController = rememberNavController()//constante para capturar el controlador de navegacion
    //NavHost es el handler de navegacion, recibe como parametro el navController y el screen inicial que se mostrara al inicio del programa
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(authViewModel, navController)
        }
        //tiene 2 elementos composable, uno por cada pantalla, donde se especifica las rutas de las mismas usando el AppScreen que creé
        composable(route = AppScreens.LoginView.route/*ruta de la pantalla authentication*/) {
            //cuando se accede a la ruta de la 1era pantalla, se llama a la funcion FirstScreen pasando como parametro el navController
            LoginView(authViewModel, navController)
        }
        composable(AppScreens.SignUpView.route) {
            SignUpView(authViewModel, navController)
        }
        composable(route = AppScreens.HomeView.route) {
            HomeView(authViewModel, navController)
        //SecondScreen(navController, it.arguments?.getString("text"))//se llama al secondScreen, pasando como argumento el navcontroller y el texto
        }
    }
}