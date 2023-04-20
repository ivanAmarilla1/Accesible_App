package com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.authentication.LoginView
import com.blessingsoftware.accesibleapp.usecases.authentication.RecoverPasswordView
import com.blessingsoftware.accesibleapp.usecases.authentication.SignUpView
import com.blessingsoftware.accesibleapp.usecases.navigation.AUTH_ROUTE
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = AppScreens.LoginView.route, route = AUTH_ROUTE) {
        //tiene 2 elementos composable, uno por cada pantalla, donde se especifica las rutas de las mismas usando el AppScreen que creé
        composable(route = AppScreens.LoginView.route/*ruta de la pantalla authentication*/) {
            //cuando se accede a la ruta de la 1era pantalla, se llama a la funcion FirstScreen pasando como parametro el navController
            LoginView(authViewModel, navController)
        }
        composable(AppScreens.SignUpView.route) {
            SignUpView(authViewModel, navController)
        }
        composable(AppScreens.RecoverPasswordView.route) {
            RecoverPasswordView(authViewModel, navController)
        }
    }
}