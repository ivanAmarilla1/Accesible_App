package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun SignUpView(viewModel: AuthViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SignUp(Modifier.padding(0.dp, 10.dp), viewModel, navController)
    }
    SignUpBackHandler(viewModel, navController)
}

@Composable
private fun SignUp(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {

    val name: String by viewModel.name.observeAsState(initial = "")
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val confirmPassword: String by viewModel.confirmPassword.observeAsState(initial = "")
    val signUpFlag = viewModel.flag.observeAsState()
    val signUpFlow = viewModel.signUpFlow.collectAsState()

    val context = LocalContext.current
    Column(modifier.verticalScroll(rememberScrollState())) {
        SignUpHeader(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(20.dp))
        SignUpNameField(name) {viewModel.onSignUpFieldsChanged(it, email, password, confirmPassword)}
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpEmailField(email) {viewModel.onSignUpFieldsChanged(name, it, password, confirmPassword)}
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpPasswordField(password) {viewModel.onSignUpFieldsChanged(name, email, it, confirmPassword)}
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpConfirmPasswordField(confirmPassword) {viewModel.onSignUpFieldsChanged(name, email, password, it)}
        Spacer(modifier = Modifier.padding(15.dp))
        SignUpButton {
            if (password == confirmPassword){
                viewModel.signUp(name, email, password)
            }else {
                //Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG)
                Log.d("SignUpButton", "Las contraseñas no coinciden")
            }
        }
        Spacer(modifier = Modifier.padding(7.dp))
        AlreadyHaveAnAccount(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(5.dp))
        OrDivider()
        Spacer(modifier = Modifier.padding(7.dp))
        SignUpWithGoogleButton()
    }

    signUpFlow.value.let {
        if (signUpFlag.value == true) {
            Log.d("signUpflow", "Ingresando a signUpflow")
            when (it) {
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppScreens.HomeView.route) {
                            popUpTo(AppScreens.LoginView.route) { inclusive = true }
                        }
                    }
                }
                is Resource.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}


@Composable
private fun SignUpHeader(modifier: Modifier) {
    Image(
        painterResource(R.drawable.user_ic), contentDescription = "Icono de usuario",
        modifier = modifier
    )
}

@Composable
private fun SignUpNameField(name: String, onSignUpFieldsChanged: (String) -> Unit) {
    TextField(
        label = { Text(text = stringResource(R.string.name), color = MaterialTheme.colors.secondaryVariant) },
        //placeholder = { Text(text = stringResource(R.string.name), color = MaterialTheme.colors.secondaryVariant) },
        value = name,
        onValueChange = { onSignUpFieldsChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.secondary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SignUpEmailField(email: String, onSignUpFieldsChanged: (String) -> Unit) {
    TextField(
        //placeholder = { Text(text = "Correo electrónico") },
        label = { Text(text = stringResource(R.string.email), color = MaterialTheme.colors.secondaryVariant) },
        value = email,
        onValueChange = { onSignUpFieldsChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.secondary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SignUpPasswordField(password: String, onSignUpFieldsChanged: (String) -> Unit) {
    TextField(
        //placeholder = { Text(text = "Contraseña") },
        label = { Text(text = stringResource(R.string.password), color = MaterialTheme.colors.secondaryVariant) },
        value = password,
        onValueChange = { onSignUpFieldsChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.secondary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}

@Composable
private fun SignUpConfirmPasswordField(confirmPassword: String, onSignUpFieldsChanged: (String) -> Unit) {
    TextField(
        //placeholder = { Text(text = "Confirme la contraseña") },
        label = { Text(text = stringResource(R.string.confirm_password), color = MaterialTheme.colors.secondaryVariant) },
        value = confirmPassword,
        onValueChange = { onSignUpFieldsChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.secondary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}

@Composable
private fun SignUpButton(onSignUpSelected: () -> Unit) {
    Button(
        onClick = { onSignUpSelected() }, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        )
    ) {
        Text(stringResource(R.string.signup), color = MaterialTheme.colors.onBackground)
    }
}

@Composable
private fun AlreadyHaveAnAccount(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.already_have_account),
        fontSize = 18.sp,
        //fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary
    )
    Text(
        text = stringResource(R.string.sign_up_here),
        modifier = modifier.clickable { },
        fontSize = 18.sp,
        //fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(
            startIndent = 8.dp, thickness = 1.dp, color = MaterialTheme.colors.secondary, modifier = Modifier
                .width(165.dp)
        )
        Text("  " +stringResource(R.string.or), color = MaterialTheme.colors.secondary)
        Divider(startIndent = 8.dp, thickness = 1.dp, color = MaterialTheme.colors.secondary)
    }
}

@Composable
private fun SignUpWithGoogleButton() {
    Button(
        onClick = {}, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            //disabledBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Image(painterResource(R.drawable.google), contentDescription = "icono google")
        Text(text = " "+stringResource(R.string.google_signin), color = MaterialTheme.colors.onBackground)
    }
}

@Composable
private fun SignUpBackHandler(viewModel: AuthViewModel, navController: NavController) {
    BackHandler(enabled = true, onBack = {
        viewModel.cleanFields()
        navController.navigate(AppScreens.LoginView.route) {
            popUpTo(AppScreens.LoginView.route) { inclusive = true }
        }

        Log.d("BackHandler","Boton atras")
    })
}



@Preview(showBackground = true, name = "Light mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SignUpScreenPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            //SignUpView(null, rememberNavController())
        }
    }
}