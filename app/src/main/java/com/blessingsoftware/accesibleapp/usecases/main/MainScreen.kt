package com.blessingsoftware.accesibleapp.usecases.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.BottomNavigationBar
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.makesuggestion.MakeSuggestionViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AUTH_ROUTE
import com.blessingsoftware.accesibleapp.usecases.navigation.AppNavigation
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
        AppNavigation(loginViewModel, homeViewModel, suggestionViewModel, navController)
        TopBar(scope, scaffoldState, navController, topBarItems)
    }
}

