package com.blessingsoftware.accesibleapp.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<AppScreens>) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = items.any { it.route == currentDestination?.route }//Para mostrar el bottomBar en las pantallas que tenga la lista items
    if (bottomBarDestination) {
        BottomNavigation (
            modifier = Modifier.height(50.dp),
            backgroundColor = MaterialTheme.colors.onSecondary
                ) {
            items.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                BottomNavigationItem(
                    icon = {
                        Icon(
                            (if (selected) screen.icon_filled else screen.icon_outlined)!!,
                            contentDescription = screen.tittle,
                            tint = MaterialTheme.colors.onBackground
                        )
                    },
                    //label = { Text(screen.tittle, color = MaterialTheme.colors.onBackground) },
                    //alwaysShowLabel = false,
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the home destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(AppScreens.HomeView.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun BottomBarPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val bottomNavigationItems = listOf(AppScreens.HomeView, AppScreens.FindWhereToGoView)
            val navController = rememberNavController()
            BottomNavigationBar(navController, bottomNavigationItems)
        }
    }
}