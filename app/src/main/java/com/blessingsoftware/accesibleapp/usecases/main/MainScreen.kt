package com.blessingsoftware.accesibleapp.usecases.main

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.ui.composables.BottomNavigationBar
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppNavigation
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier,
    loginViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    suggestionViewModel: MakeSuggestionViewModel,
    navController: NavHostController
) {

    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()
    val bottomNavigationItems = listOf(AppScreens.HomeView, AppScreens.RandomView)
    val drawerItems = listOf(
        AppScreens.HomeView,
        AppScreens.MakeSuggestion,
        AppScreens.ItemOne,
        AppScreens.ItemTwo,
        AppScreens.ItemThree
    )
    val topBarItems = listOf(
        AppScreens.HomeView,
        AppScreens.MakeSuggestion,
        AppScreens.ItemOne,
        AppScreens.ItemTwo,
        AppScreens.ItemThree,
        AppScreens.RandomView
    )
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController, items = bottomNavigationItems) },
        //topBar = {  },
        drawerContent = {
            NavigationDrawer(
                scope,
                scaffoldState,
                navController,
                drawerItems,
                loginViewModel
            )
        },
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) {
        AppNavigation(loginViewModel, homeViewModel, suggestionViewModel, navController, scaffoldState)
        TopBar(scope, scaffoldState, navController, topBarItems)
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen, onBack = {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    })
}

@Composable
private fun DrawerBackHandler(viewModel: AuthViewModel, navController: NavController) {
    BackHandler(enabled = true, onBack = {
        navController.navigate(AppScreens.LoginView.route) {
            popUpTo(AppScreens.LoginView.route) { inclusive = true }
        }
        viewModel.cleanFields()
        //Log.d("BackHandler", "Boton atras")
    })
}

