package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ReusableTittle(
    tittle: String = "",
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    style: TextStyle = MaterialTheme.typography.h5,
    textAlign: TextAlign = TextAlign.Start
    ) {
    Text(
        tittle,
        modifier = modifier,
        color = textColor,
        style = style,
        textAlign = textAlign,
    )
}

@Composable
fun ReusableSubtitle(
    subtittle: String = "",
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    style: TextStyle = MaterialTheme.typography.h6,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        subtittle,
        modifier = modifier,
        color = textColor,
        style = style,
        textAlign = textAlign,
    )
}

@Composable
fun ReusableTextBody(
    body: String = "",
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    style: TextStyle = MaterialTheme.typography.body1,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        body,
        modifier = modifier,
        color = textColor,
        style = style,
        textAlign = textAlign,
    )
}