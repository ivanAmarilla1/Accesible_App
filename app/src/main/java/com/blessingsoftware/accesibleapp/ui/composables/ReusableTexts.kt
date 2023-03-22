package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun ReusableTittle(
    tittle: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    style: TextStyle = MaterialTheme.typography.h5,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1000,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Text(
        tittle,
        modifier = modifier,
        color = textColor,
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = textOverflow
    )
}

@Composable
fun ReusableSubtitle(
    subtittle: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    style: TextStyle = MaterialTheme.typography.h6,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1000,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Text(
        subtittle,
        modifier = modifier,
        color = textColor,
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = textOverflow
    )
}

@Composable
fun ReusableTextBody(
    body: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.secondary,
    //fontSize: TextStyle = 24.sp,
    style: TextStyle = MaterialTheme.typography.body1,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1000,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Text(
        body,
        modifier = modifier,
        color = textColor,
        //fontSize = fontSize,
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = textOverflow
    )
}