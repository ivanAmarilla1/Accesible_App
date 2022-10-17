package com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.blessingsoftware.accesibleapp.ui.composables.BottomNavigationBar
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeView
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.home.RandomView
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    navigation(startDestination = AppScreens.HomeView.route, route = HOME_ROUTE) {
        composable(route = AppScreens.HomeView.route) {
            HomeView(homeViewModel, navController)
        }
        composable(AppScreens.RandomView.route) {
            RandomView(navController)
        }
    }
}