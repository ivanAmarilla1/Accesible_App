package com.blessingsoftware.accesibleapp.usecases.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.ReusableTextBody
import com.blessingsoftware.accesibleapp.ui.composables.ReusableTittle

@Composable
fun ItemThree(navController: NavController) {
    Box(modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 50.dp)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(10.dp))
            ReusableTittle(tittle = stringResource(R.string.what_is_asuncion_accesible))
            Spacer(modifier = Modifier.height(10.dp))
            ReusableTextBody(body = stringResource(R.string.app_explanation) )

            Spacer(modifier = Modifier.height(30.dp))
            ReusableTittle(tittle = stringResource(R.string.what_is_make_suggestion))
            Spacer(modifier = Modifier.height(10.dp))
            ReusableTextBody(body = stringResource(R.string.make_suggestion_explanation) )

            Spacer(modifier = Modifier.height(30.dp))
            ReusableTittle(tittle = stringResource(R.string.help_us_improve))
            Spacer(modifier = Modifier.height(10.dp))
            ReusableTextBody(body = stringResource(R.string.text_hel_us_improve) )
            Spacer(modifier = Modifier.height(10.dp))

        }
    }
}