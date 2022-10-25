package com.blessingsoftware.accesibleapp.usecases.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AUTH_ROUTE
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    items: List<AppScreens>,
    auhViewModel: AuthViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Column() {
        Row(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                //.background(MaterialTheme.colors.onSecondary)
                .padding(20.dp, 25.dp, 0.dp, 0.dp)

        ) {
            UserImage()
            Spacer(modifier = Modifier.padding(10.dp))
            Column(Modifier.padding(top = 15.dp)) {
                auhViewModel.currentUser?.displayName?.let {
                    Text(
                        it.trim(),
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.secondary
                    )
                }
                Text(
                    text = stringResource(R.string.edit_account),
                    modifier = Modifier.clickable { },//TODO Editar perfil
                    fontSize = 14.sp,
                    //fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary
                )
            }

        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            startIndent = 8.dp,
            thickness = 1.dp,
            color = MaterialTheme.colors.secondary
        )
        Spacer(
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth()
        )

        items.forEach { item ->
            DrawerItem(item, currentDestination?.route == item.route) {
                navController.navigate(item.route) {
                    popUpTo(AppScreens.HomeView.route) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 15.dp, bottom = 15.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            LogOutButton(auhViewModel, navController, scaffoldState, scope)
        }

    }
}

@Composable
fun DrawerItem(
    item: AppScreens,
    selected: Boolean,
    onItemClick: (AppScreens) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(6.dp)
            .clip(RoundedCornerShape(12))
            .background(if (selected) MaterialTheme.colors.onSecondary else Color.Transparent)
            .padding(8.dp)
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = if (selected) item.icon_filled!! else item.icon_outlined!!,
            contentDescription = item.tittle,
            tint = if (selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.tittle,
            style = TextStyle(fontSize = 18.sp),
            color = if (selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun UserImage() {
    Image(//llamada a una imagen para mostrar en pantalla
        painter = painterResource(R.drawable.user_ic),//TODO agregar imagen de usuario
        contentDescription = "User image",
        modifier = Modifier//modificadores de tamaño, forma y fondo
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.White)
    )
}

@Composable
private fun LogOutButton(
    viewModel: AuthViewModel?,
    navController: NavController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    Button(
        onClick = {
            scope.launch {
                scaffoldState.drawerState.close()
            }
            viewModel?.logOut(context)
            navController.navigate(AUTH_ROUTE) {
                popUpTo(AppScreens.HomeView.route) { inclusive = true }
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        ),
    ) {
        Text(stringResource(R.string.logout), color = MaterialTheme.colors.onBackground)
    }


}
