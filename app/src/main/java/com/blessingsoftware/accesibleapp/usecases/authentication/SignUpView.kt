package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens

@Composable
fun SignUpView(viewModel: AuthViewModel?, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SignUp(Modifier.padding(0.dp, 10.dp), viewModel, navController)
    }
    SignUpBackHandler(navController)
}

@Composable
private fun SignUp(modifier: Modifier, viewModel: AuthViewModel?, navController: NavController) {
    //val scrollState = rememberScrollState()
    Column(modifier.verticalScroll(rememberScrollState())) {
        SignUpHeader(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(20.dp))
        SignUpNameField("")
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpEmailField("")
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpPasswordField("")
        Spacer(modifier = Modifier.padding(10.dp))
        SignUpConfirmPasswordField("")
        Spacer(modifier = Modifier.padding(15.dp))
        SignUpButton()
        Spacer(modifier = Modifier.padding(7.dp))
        AlreadyHaveAnAccount(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(5.dp))
        OrDivider()
        Spacer(modifier = Modifier.padding(7.dp))
        SignUpWithGoogleButton()
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
private fun SignUpNameField(name: String /*onTextFieldChanged: (String) -> Unit*/) {
    TextField(
        //label = { Text(text = "Nombre") },
        placeholder = { Text(text = "Nombre") },
        value = name,
        onValueChange = { /*onTextFieldChanged(it)*/ },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            textColor = MaterialTheme.colors.primary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SignUpEmailField(email: String /*onTextFieldChanged: (String) -> Unit*/) {
    TextField(
        //label = { Text(text = "Correo electrónico") },
        placeholder = { Text(text = "Correo electrónico") },
        value = email,
        onValueChange = { /*onTextFieldChanged(it)*/ },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            textColor = MaterialTheme.colors.primary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SignUpPasswordField(password: String /*onTextFieldChanged: (String) -> Unit*/) {
    TextField(
        //label = { Text(text = "Contraseña") },
        placeholder = { Text(text = "Contraseña") },
        value = password,
        onValueChange = { /*onTextFieldChanged(it)*/ },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            textColor = MaterialTheme.colors.primary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}

@Composable
private fun SignUpConfirmPasswordField(confirmPassword: String /*onTextFieldChanged: (String) -> Unit*/) {
    TextField(
        //label = { Text(text = "Confirme la contraseña") },
        placeholder = { Text(text = "Confirme la contraseña") },
        value = confirmPassword,
        onValueChange = { /*onTextFieldChanged(it)*/ },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.textFieldColors(
            //TODO Aplicar tema de colores de material design
            textColor = MaterialTheme.colors.primary,
            //backgroundColor = Color(0xFFEEECEC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

    )
}

@Composable
private fun SignUpButton(/*onSignUpSelected: () -> Unit*/) {
    Button(
        onClick = { /*onSignUpSelected() */ }, modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = Color.DarkGray
        )
    ) {
        Text("Registrarse")
    }
}

@Composable
private fun AlreadyHaveAnAccount(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = "Ya tienes una cuenta?",
        fontSize = 18.sp,
        //fontWeight = FontWeight.Bold,
        color = Color.Blue
    )
    Text(
        text = "Inicia sesión aquí",
        modifier = modifier.clickable { },
        fontSize = 18.sp,
        //fontWeight = FontWeight.Bold,
        color = Color.Blue
    )
}

@Composable
fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(
            startIndent = 8.dp, thickness = 1.dp, color = Color.Blue, modifier = Modifier
                .width(175.dp)
        )
        Text("  O")
        Divider(startIndent = 8.dp, thickness = 1.dp, color = Color.Blue)
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
            disabledBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Image(painterResource(R.drawable.google), contentDescription = "icono google")
        Text(text = " Ingresar con Google")
    }
}

@Composable
private fun SignUpBackHandler(navController: NavController) {
    BackHandler(enabled = true, onBack = {
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
            SignUpView(null, rememberNavController())
        }
    }
}