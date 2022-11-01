package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs.authNavGraph
import com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs.homeNavGraph
import com.blessingsoftware.accesibleapp.usecases.splashscreen.SplashScreen


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    navController: NavHostController,
    scaffoldState: ScaffoldState
) {
    //NavHost es el handler de navegacion, recibe como parametro el navController y el screen inicial que se mostrara al inicio del programa
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route, route = ROOT_ROUTE) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(authViewModel, navController)
        }
        authNavGraph(navController, authViewModel)
        homeNavGraph(navController, homeViewModel, suggestionViewModel, authViewModel, scaffoldState)
    }
}