package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch

//TODO Ver inyeccion de dependencias
@Composable
fun LoginView(viewModel: AuthViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        login(Modifier.align(Alignment.Center), viewModel, navController)
    }

}

@Composable
fun login(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {
    //engancha la vista al live data del viewmodel
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    //val buttonEnabler: Boolean by viewModel.buttonEnabler.observeAsState(initial = false)
    //val coroutineScope = rememberCoroutineScope()//variable para ejecutar corrutinas
    //var email by remember { mutableStateOf("") }
    //var password by remember { mutableStateOf("") }
    var loginFlag = viewModel.flag.observeAsState()
    val loginFlow = viewModel.loginFlow.collectAsState()

    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField(email) { viewModel.onFieldsChanged(it, password) }
        Spacer(modifier = Modifier.padding(6.dp))
        PasswordField(password) { viewModel.onFieldsChanged(email, it) }
        Spacer(modifier = Modifier.padding(6.dp))
        ForgotPassword(Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.padding(10.dp))
        LoginButton() {
            viewModel.login(email, password)
        }
        Spacer(modifier = Modifier.padding(16.dp))
        DontHaveAccount(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(4.dp))
        RegisterButton { }
        Spacer(modifier = Modifier.padding(6.dp))
        SignUpWithGoogleButton()
    }

    loginFlow.value.let {
        if (loginFlag.value == true) {
            Log.d("loginflow", "Ingresando a loginflow")
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
fun HeaderImage(modifier: Modifier) {
    Image(
        painterResource(R.drawable.user_ic), contentDescription = "Icono de usuario",
        modifier = modifier
    )
}

@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        label = { Text(text = "Email") },
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            //textColor = Color(0xFF020202),
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            //textColor = Color(0xFF020202),
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "Olvidaste la contraseña?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Blue
    )
}

@Composable
fun LoginButton(onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() }, modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        )
    ) {
        Text("Iniciar Sesión")
    }
}

@Composable
fun DontHaveAccount(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = "No tienes una cuenta?",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Blue
    )
}

@Composable
fun RegisterButton(registerUser: () -> Unit) {
    Button(
        onClick = { registerUser() }, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Text("Registrarse")
    }
}

@Composable
fun SignUpWithGoogleButton() {
    Button(
        onClick = {}, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Image(painterResource(R.drawable.google), contentDescription = "icono google")
        Text(text = " Ingresar con Google")
    }
}

@Preview(showBackground = true, name = "Light mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun MainScreenPreview() {
    AccesibleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            //LoginView(null, rememberNavController())
        }
    }
}