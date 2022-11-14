package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CustomDialog(
    buttonText: String,
    tittle: String,
    text: String,
    image: Painter,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        CustomDialogUI(Modifier, buttonText, tittle, text, image, { onDismissRequest() }) {
            onConfirmRequest()
        }
    }
}

//Layout
@Composable
private fun CustomDialogUI(
    modifier: Modifier = Modifier,
    buttonText: String,
    tittle: String,
    text: String,
    image: Painter,
    onDeclineRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    Card(
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(10.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(MaterialTheme.colors.onSecondary)
        ) {
            //.......................................................................
            Image(
                painter = image,
                contentDescription = null, // decorative
                contentScale = ContentScale.Fit,
                /*colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colors.secondary
                ),*/
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),

                )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = tittle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.body1
                )
            }
            //.......................................................................
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colors.onSecondary),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(onClick = {
                    onDeclineRequest()
                    //openDialogCustom.value = false
                }) {

                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Bold,
                        //color = PurpleGrey40,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    onConfirmRequest()
                    //openDialogCustom.value = false
                }) {
                    Text(
                        buttonText,
                        fontWeight = FontWeight.ExtraBold,
                        //color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}
