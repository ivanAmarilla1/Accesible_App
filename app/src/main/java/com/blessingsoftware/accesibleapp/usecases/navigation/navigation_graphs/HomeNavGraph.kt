package com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeView
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.home.RandomView
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestion
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE
import com.blessingsoftware.accesibleapp.usecases.test.ItemOne
import com.blessingsoftware.accesibleapp.usecases.test.ItemThree
import com.blessingsoftware.accesibleapp.usecases.test.ItemTwo
import com.google.firebase.auth.FirebaseUser

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    currentUser: FirebaseUser?,
) {
    navigation(startDestination = AppScreens.HomeView.route, route = HOME_ROUTE) {
        composable(route = AppScreens.HomeView.route) {
            HomeView(homeViewModel, navController)
        }
        composable(AppScreens.RandomView.route) {
            RandomView(navController)
        }
        composable(AppScreens.MakeSuggestion.route) {
            MakeSuggestion(homeViewModel, suggestionViewModel, currentUser)
        }
        composable(AppScreens.ItemOne.route) {
            ItemOne(navController)
        }
        composable(AppScreens.ItemTwo.route) {
            ItemTwo(navController)
        }
        composable(AppScreens.ItemThree.route) {
            ItemThree(navController)
        }


    }
}