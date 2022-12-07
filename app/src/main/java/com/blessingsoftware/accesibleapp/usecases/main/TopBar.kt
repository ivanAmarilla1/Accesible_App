package com.blessingsoftware.accesibleapp.usecases.main

import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    items: List<AppScreens>,
    drawerItems: List<AppScreens>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val drawerDestination = drawerItems.any { it.route == currentDestination?.route }

    var title = "Asunción Accesible"
    items.forEach{
        if (it.route == currentDestination?.route){
            title = it.tittle
        }
    }

    val topBarDestination =
        items.any { it.route == currentDestination?.route }//Para mostrar el topBar en las pantallas que tenga la lista items
    if (topBarDestination) {
        TopAppBar(
            modifier = Modifier.height(50.dp),
            elevation = 0.dp,
            title = {
                (if (currentDestination?.route != AppScreens.HomeView.route) title else "")?.let {
                    Text(
                        text = it,//LocalContext.current.getString(R.string.app_name),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            },//titulo del TopBar
            navigationIcon = {//Icono de 3 lineas de la parte izquierda
                if (drawerDestination){
                    IconButton(onClick = {
                        //al hacer click se abre el drawer
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu icon",
                            tint = if (currentDestination?.route != AppScreens.HomeView.route) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface
                        )
                    }
                } else {
                    IconButton(onClick = {
                        //al hacer click se abre el drawer
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Arrow Back",
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }



            },
            /*actions = {
                IconButton(onClick = { /*TODO*/ }) {//iconos de la parte derecha del TopBar
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Menu icon", tint = MaterialTheme.colors.onSurface)
                }
            },*/
            backgroundColor = Color.Transparent//MaterialTheme.colors.onSecondary
        )
    }
}