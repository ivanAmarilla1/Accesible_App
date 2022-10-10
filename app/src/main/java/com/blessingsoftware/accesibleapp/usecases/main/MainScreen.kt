package com.blessingsoftware.accesibleapp.usecases.main

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.ui.composables.BottomNavigationBar
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppNavigation
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun MainScreen(
    loginViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    val navigationItems = listOf(AppScreens.HomeView, AppScreens.RandomView)
    Scaffold(bottomBar = { BottomNavigationBar(navController, items = navigationItems) }) {
        AppNavigation(loginViewModel, homeViewModel, navController)
    }
}