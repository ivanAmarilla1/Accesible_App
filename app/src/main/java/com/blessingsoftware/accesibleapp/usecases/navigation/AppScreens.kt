package com.blessingsoftware.accesibleapp.usecases.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

const val ROOT_ROUTE = "root"
const val AUTH_ROUTE = "auth"
const val HOME_ROUTE = "home"


sealed class AppScreens(
    val route: String, val tittle: String, val icon_outlined: ImageVector?, val icon_filled: ImageVector?) {
    //declaracion de las rutas de las pantallas

    //Splash
    object SplashScreen : AppScreens("splash_screen", "Splash Screen", null, null)

    //Auth rutes
    object LoginView : AppScreens("login_view", "Iniciar Sesión",null, null)
    object SignUpView : AppScreens("signup_view", "Home", null, null)
    object RecoverPasswordView : AppScreens("recover_password_view", "Recuperar contraseña", null, null)

    //Home routes
    object HomeView : AppScreens("home_view", "Inicio", Icons.Outlined.Home, Icons.Filled.Home )
    object MakeSuggestion : AppScreens("make_suggestion", "Realizar Sugerencia", Icons.Outlined.AddLocationAlt, Icons.Filled.AddLocationAlt)
    object FindWhereToGoView : AppScreens("find_where_to_go_view", "¿Qué estás buscando?", Icons.Outlined.Search, Icons.Filled.Search)
    object PlaceTypeSelected : AppScreens("place_type_selected", "Lugares", Icons.Outlined.Search, Icons.Filled.Search)
    object SelectedPlace : AppScreens("selected_place", "Lugar", Icons.Outlined.Search, Icons.Filled.Search)


    object SuggestionList : AppScreens("suggestion_list", "Lista de Sugerencias", Icons.Outlined.List, Icons.Filled.List)
    object SuggestionDetail : AppScreens("suggestion_detail", "Detalle de Sugerencia", Icons.Outlined.Details, Icons.Filled.Details)

    object ItemOne : AppScreens("item_one", "Item One", Icons.Outlined.Search, Icons.Filled.Search)
    object ItemTwo : AppScreens("item_two", "Configuración", Icons.Outlined.Settings, Icons.Filled.Settings)
    object ItemThree : AppScreens("item_three", "Acerca de", Icons.Outlined.QuestionMark, Icons.Filled.QuestionMark)

}