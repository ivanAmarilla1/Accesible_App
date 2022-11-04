package com.blessingsoftware.accesibleapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Primary,//Botones o cosas de la interfaz principal, etc
    primaryVariant = PimaryVariant,//reservado para otro color de la interfaz
    secondary = White,//Texto de la app
    secondaryVariant = DarkModeSecondaryTextColor,//placeholders
    onSecondary = DarkModeSecondary,//Fondo de Textfields
    onBackground = White,//Texto de los botones
    onSurface = Black,//Sobre mapa
    background = DarkModeBackground,//fondo de la app


)


private val LightColorPalette = lightColors(
    primary = Primary,//Botones, interfaz principal, etc
    primaryVariant = PimaryVariant,//reservado para otro color de la interfaz
    secondary = LightModeText,//Texto de la app
    secondaryVariant = LightModeSecondaryTextColor,//placeholders
    onSecondary = LightModeSecondary,//Fondo de Textfields
    onBackground = White,//Texto de los botones
    onSurface = Black,//Sobre mapa
    background = LightModeBackground,//fondo de la app

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AccesibleAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val systemBarColor = Color.Transparent
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    val systemUIController = rememberSystemUiController()
    SideEffect {
        systemUIController.setStatusBarColor(
            color = systemBarColor,
            darkIcons = !darkTheme,
        )
    }


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}