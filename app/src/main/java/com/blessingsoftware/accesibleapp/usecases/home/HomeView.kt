package com.blessingsoftware.accesibleapp.usecases.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.authentication.AuthViewModel
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens


@Composable
fun HomeView(viewModel: AuthViewModel?, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Home(Modifier.align(Alignment.Center), viewModel, navController)
    }
}

@Composable
fun Home(modifier: Modifier, viewModel: AuthViewModel?, navController: NavController) {
    Column(modifier = modifier) {
        Text("Home Screen")
        viewModel?.currentUser?.email?.let { Text(it?.trim()) }
        Spacer(modifier = Modifier.padding(16.dp))
        LogOutButton(viewModel, navController)
    }

}

@Composable
fun LogOutButton(viewModel: AuthViewModel?, navController: NavController) {
    Button(
        onClick = {
            viewModel?.logOut()
            navController.navigate(AppScreens.LoginView.route) {
                popUpTo(AppScreens.HomeView.route) { inclusive = true }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        ),
    ) {
        Text("Cerrar Sesión")
    }
}

@Preview(showBackground = true, name = "Light mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun HomeScreenPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            HomeView(null, rememberNavController())
        }
    }
}