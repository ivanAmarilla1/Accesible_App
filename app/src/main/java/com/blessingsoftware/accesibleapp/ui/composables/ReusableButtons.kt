package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.R


@Composable
fun CustomGoogleButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.google_signin),
    loadingText: String = stringResource(R.string.google_signin),
    icon: Int = R.drawable.google,
    isLoading: Boolean = false,
    shape: Shape = RoundedCornerShape(20.dp),
    //borderColor: Color = Color.LightGray,
    //backgroundColor: Color = MaterialTheme.colors.primary,
    progressIndicatorColor: Color = MaterialTheme.colors.onBackground,
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { onClicked() },
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary
        ),
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Google Button",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (clicked) loadingText else text, color = MaterialTheme.colors.onBackground)
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )

            }
        }
    }
}

@Composable
@Preview
private fun GoogleButtonPreview() {
    CustomGoogleButton(
        text = "Sign Up with Google",
        loadingText = "Creating Account...",
        onClicked = {},
        shape = RoundedCornerShape(20.dp)
    )
}