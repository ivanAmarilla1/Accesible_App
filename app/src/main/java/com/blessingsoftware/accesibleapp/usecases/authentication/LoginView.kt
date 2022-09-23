package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

//TODO Ver inyeccion de dependencias
@Composable
fun LoginView(viewModel: AuthViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel, navController)
    }

}

@Composable
private fun Login(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {
    //engancha la vista al live data del viewmodel
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginFlag = viewModel.flag.observeAsState()
    val loginFlow = viewModel.loginFlow.collectAsState()

    //para la autenticacion con google
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)

    // val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
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
        RegisterButton { viewModel.cleanFields(); navController.navigate(AppScreens.SignUpView.route) }
        Spacer(modifier = Modifier.padding(6.dp))
        SignUpWithGoogleButton(context, token, viewModel)
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
private fun HeaderImage(modifier: Modifier) {
    Image(
        painterResource(R.drawable.user_ic), contentDescription = "Icono de usuario",
        modifier = modifier
    )
}

@Composable
private fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        label = { Text(text = stringResource(R.string.email), color = MaterialTheme.colors.secondaryVariant)},
        value = email,
        onValueChange = { onTextFieldChanged(it) },
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
private fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        label = { Text(text = stringResource(R.string.password), color = MaterialTheme.colors.secondaryVariant) },
        value = password,
        onValueChange = { onTextFieldChanged(it) },
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
private fun ForgotPassword(modifier: Modifier) {
    Text(
        text = stringResource(R.string.forgot_password),
        modifier = modifier.clickable { TODO() },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
private fun LoginButton(onLoginSelected: () -> Unit) {
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
        Text(stringResource(R.string.login), color = MaterialTheme.colors.onBackground)
    }
}

@Composable
private fun DontHaveAccount(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.dont_have_account),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
private fun RegisterButton(registerUser: () -> Unit) {
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
        Text(stringResource(R.string.signup), color = MaterialTheme.colors.onBackground)
    }
}

@Composable
private fun SignUpWithGoogleButton(context: Context, token: String, viewModel: AuthViewModel) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signUpWithGoogle(credential)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }
        }
    OutlinedButton(
        border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            disabledBackgroundColor = MaterialTheme.colors.primary
        ),
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },

        ) {
        Image(painterResource(R.drawable.google), contentDescription = "icono google")
        Text(text = " "+stringResource(R.string.google_signin), color = MaterialTheme.colors.onBackground)
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
private fun MainScreenPreview() {
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