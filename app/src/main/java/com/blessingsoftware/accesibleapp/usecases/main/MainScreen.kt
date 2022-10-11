package com.blessingsoftware.accesibleapp.usecases.main

import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
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
    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()
    val bottomNavigationItems = listOf(AppScreens.HomeView, AppScreens.RandomView)
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController, items = bottomNavigationItems) },
        topBar = { TopBar(scope, scaffoldState,navController, bottomNavigationItems) },
        drawerContent = {
            NavigationDrawer(
                scope,
                scaffoldState,
                navController,
                bottomNavigationItems,
                loginViewModel
            )
        }


    ) {
        AppNavigation(loginViewModel, homeViewModel, navController)
    }
}