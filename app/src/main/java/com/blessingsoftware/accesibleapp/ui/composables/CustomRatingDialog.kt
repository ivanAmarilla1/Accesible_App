package com.blessingsoftware.accesibleapp.ui.composables

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.blessingsoftware.accesibleapp.R

@Composable
fun CustomRatingDialog(
    buttonText: String,
    rating: Int,
    validateRate: Boolean,
    onDismissRequest: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onConfirmRequest: (Int) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        CustomRatingDialogUI(Modifier.fillMaxWidth(), rating, validateRate, buttonText, { onDismissRequest() }, {onRatingChange(it)}) {
           onConfirmRequest(it)
        }
    }
}

//Layout
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CustomRatingDialogUI(
    modifier: Modifier = Modifier,
    rating: Int,
    validateRate: Boolean,
    buttonText: String,
    onDeclineRequest: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onConfirmRequest: (Int) -> Unit
) {

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp, 5.dp, 10.dp, 10.dp)
            .size(450.dp, 240.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(MaterialTheme.colors.onSecondary)
        ) {
            //.......................................................................
            Text(
                text = "Ingrese su calificación",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(30.dp))
            RateBar(
                Modifier,
                rating = rating,
                validateRate = validateRate,
                validateRateError = "Ingrese su calificación",
            ) {
                onRatingChange(it)
            }
            //.......................................................................
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colors.onSecondary),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(onClick = {
                    onDeclineRequest()
                }) {

                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    onConfirmRequest(rating)
                }) {
                    Text(
                        buttonText,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
private fun RateBar(
    modifier: Modifier = Modifier,
    rating: Int,
    validateRate: Boolean,
    validateRateError: String,
    onRatingChange: (Int) -> Unit,
) {
    var ratingState = rating

    var selected by remember {
        mutableStateOf(false)
    }
    val size by animateDpAsState(
        targetValue = if (selected) 50.dp else 45.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                if (validateRate) BorderStroke(
                    2.dp,
                    MaterialTheme.colors.error
                ) else BorderStroke(2.dp, Color.Transparent), shape = RoundedCornerShape(10.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                ratingState = i
                                onRatingChange(ratingState)
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    if (validateRate) {
        Text(
            text = validateRateError,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(y = (-8).dp)
                .fillMaxWidth(0.9f)
        )
    }
}