package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.usecases.home.HomeView
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.login.LoginView
import com.blessingsoftware.accesibleapp.usecases.login.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()//constante para capturar el controlador de navegacion
    //NavHost es el handler de navegacion, recibe como parametro el navController y el screen inicial que se mostrara al inicio del programa
    NavHost(navController = navController, startDestination = AppScreens.LoginView.route) {
        //tiene 2 elementos composable, uno por cada pantalla, donde se especifica las rutas de las mismas usando el AppScreen que creé
        composable(route = AppScreens.LoginView.route/*ruta de la pantalla login*/) {
            //cuando se accede a la ruta de la 1era pantalla, se llama a la funcion FirstScreen pasando como parametro el navController
            LoginView(LoginViewModel(), navController)
        }
        composable(route = AppScreens.HomeView.route) {
            HomeView(HomeViewModel(), navController)
        //SecondScreen(navController, it.arguments?.getString("text"))//se llama al secondScreen, pasando como argumento el navcontroller y el texto
        }
    }
}