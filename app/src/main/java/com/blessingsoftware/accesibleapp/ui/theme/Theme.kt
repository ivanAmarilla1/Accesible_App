package com.blessingsoftware.accesibleapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = LightBlue700,
    primaryVariant = LightBlue300,
    secondary = LightBlue100,
    onBackground = White,
    background = DarkModeBackground,//fondo de la app
)


private val LightColorPalette = lightColors(
    primary = LightBlue700,//Botones, interfaz, etc
    primaryVariant = LightBlue300,//reservado para otro color de la interfaz
    secondary = Gray700,//Texto de la app
    secondaryVariant = Gray500,//placeholders
    onBackground = White,//Texto de los botones
    background = Gray100,//fondo de la app

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

    val colors = if (darkTheme) {
        DarkColorPalette

    } else {
        LightColorPalette
    }

    val systemUIController = rememberSystemUiController()
    SideEffect {
        systemUIController.setSystemBarsColor(
            color = LightBlue700
        )
    }


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}