package com.blessingsoftware.accesibleapp.usecases.main

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.blessingsoftware.accesibleapp.ui.composables.BottomNavigationBar
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.home.HomeViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppNavigation
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    loginViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    //Checkea si el usuario es administrador
    val isAdmin = loginViewModel.isUserAdmin.observeAsState()


    //Muestra u oculta el BottomBar
    val isBottomBarVisible = homeViewModel.isBottomBarVisible.observeAsState(initial = true)


    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )

    val bottomNavigationItems = listOf(AppScreens.HomeView, AppScreens.FindWhereToGoView)
    val drawerItems = if (isAdmin.value == true) {
        listOf(
            AppScreens.HomeView,
            AppScreens.FindWhereToGoView,
            AppScreens.MakeSuggestion,
            AppScreens.SuggestionList,
            //AppScreens.ItemTwo,
            AppScreens.ItemThree,
        )
    } else {
        listOf(
            AppScreens.HomeView,
            AppScreens.FindWhereToGoView,
            AppScreens.MakeSuggestion,
            //AppScreens.ItemTwo,
            AppScreens.ItemThree,
        )
    }

    val topBarItems = listOf(
        AppScreens.HomeView,
        AppScreens.MakeSuggestion,
        AppScreens.SuggestionList,
        //AppScreens.ItemTwo,
        AppScreens.ItemThree,
        AppScreens.FindWhereToGoView,
        AppScreens.SuggestionDetail
    )
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = { if (isBottomBarVisible.value == true) {BottomNavigationBar(navController, items = bottomNavigationItems)} },
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
            AppNavigation(
                loginViewModel,
                homeViewModel,
                navController,
                scaffoldState
            )
            TopBar(scope, scaffoldState, navController, topBarItems, drawerItems)
        }
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

