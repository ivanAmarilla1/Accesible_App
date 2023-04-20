package com.blessingsoftware.accesibleapp.usecases.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.ui.composables.ReusableSubtitle
import com.blessingsoftware.accesibleapp.ui.composables.ReusableTittle
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun RecoverPasswordView(viewModel: AuthViewModel, navController: NavController) {

    val recoverEmail: String by viewModel.recoverEmail.observeAsState(initial = "")

    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            ReusableTittle(tittle = "¿Has Olvidado la Contraseña?", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.padding(20.dp))
            ReusableSubtitle(subtittle = "Ingresa tu e-mail y te enviaremos un correo para recuperar tu contraseña", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.padding(20.dp))
            RecoverEmailField(recoverEmail) {
                viewModel.onRecoverFieldChanged(it)
            }
            Spacer(modifier = Modifier.padding(20.dp))
            RecoverPasswordButton {
                viewModel.recoverUserPassword(recoverEmail, context)
            }
        }
    }

    RecoverPasswordBackHandler(viewModel, navController)
}

@Composable
fun RecoverPasswordBackHandler(viewModel: AuthViewModel, navController: NavController) {
    BackHandler(enabled = true, onBack = {
        navController.navigate(AppScreens.LoginView.route) {
            popUpTo(AppScreens.LoginView.route) { inclusive = true }
        }
        viewModel.cleanRecoverField()
    })
}

@Composable
fun RecoverEmailField(recoverEmail: String, onTextFieldChanged: (String) -> Unit) {
    CustomOutlinedTextField(
        value = recoverEmail,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.email),
        leadingIconImageVector = Icons.Default.AlternateEmail,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
    )
}

@Composable
private fun RecoverPasswordButton(recoverPassword: () -> Unit) {
    Button(
        onClick = { recoverPassword() }, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Text("Enviar", color = MaterialTheme.colors.onBackground)
    }
}


