package com.blessingsoftware.accesibleapp.usecases.navigation.navigation_graphs

import androidx.compose.material.ScaffoldState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.*
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestion
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE
import com.blessingsoftware.accesibleapp.usecases.reviewsuggestions.ReviewSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.reviewsuggestions.ViewSuggestionDetail
import com.blessingsoftware.accesibleapp.usecases.reviewsuggestions.ViewSuggestionList
import com.blessingsoftware.accesibleapp.usecases.test.ItemThree
import com.blessingsoftware.accesibleapp.usecases.test.ItemTwo

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    authViewModel: AuthViewModel,
    reviewSuggestionViewModel: ReviewSuggestionViewModel,
    scaffoldState: ScaffoldState,
) {

    navigation(startDestination = AppScreens.HomeView.route, route = HOME_ROUTE) {
        composable(route = AppScreens.HomeView.route) {
            HomeView(homeViewModel, navController, authViewModel)
        }
        composable(AppScreens.FindWhereToGoView.route) {
            FindWhereToGoView(homeViewModel, navController, scaffoldState)
        }
        composable(AppScreens.PlaceTypeSelected.route) {
            PlaceTypeSelected(homeViewModel, navController)
        }

        composable(AppScreens.SelectedPlace.route) {
            SelectedPlace(homeViewModel, navController, authViewModel)
        }

        composable(AppScreens.MakeSuggestion.route) {
            MakeSuggestion(suggestionViewModel, navController, authViewModel, scaffoldState)
        }
        composable(AppScreens.SuggestionList.route) {
            ViewSuggestionList(reviewSuggestionViewModel, navController, scaffoldState)
        }
        composable(AppScreens.SuggestionDetail.route) {
            ViewSuggestionDetail(reviewSuggestionViewModel, navController)
        }
        composable(AppScreens.ItemTwo.route) {
            ItemTwo(navController)
        }
        composable(AppScreens.ItemThree.route) {
            ItemThree(navController)
        }


    }
}