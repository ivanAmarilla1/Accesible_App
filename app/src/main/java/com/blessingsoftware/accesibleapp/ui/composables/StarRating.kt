package com.blessingsoftware.accesibleapp.ui.composables

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R

@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    validateRate: Boolean,
    validateRateError: String,
    onRatingChange: (Int) ->Unit,
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
        modifier = Modifier.fillMaxSize(0.65f)
        .border(if (validateRate) BorderStroke(2.dp, MaterialTheme.colors.error) else BorderStroke(2.dp, Color.Transparent), shape = RoundedCornerShape(10.dp)
    ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
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