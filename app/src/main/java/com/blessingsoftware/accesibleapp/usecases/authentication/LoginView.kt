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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.CustomGoogleButton
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.blessingsoftware.accesibleapp.usecases.navigation.HOME_ROUTE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


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

    //Validacion
    val focusManager = LocalFocusManager.current
    val validateEmail = viewModel.validateEmail.observeAsState()
    val validatePassword = viewModel.validatePassword.observeAsState()
    val validateEmailError = stringResource(R.string.validate_email)
    val validatePasswordError = stringResource(R.string.validate_password)
    val isPasswordVissible = viewModel.passwordVisibility.observeAsState()

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField(email, validateEmail.value, validateEmailError, focusManager) {
            viewModel.onFieldsChanged(
                it,
                password
            )
        }
        PasswordField(password, validatePassword.value, validatePasswordError, isPasswordVissible.value, focusManager, { viewModel.onPasswordVisibilityChanges(it)})
        {
            viewModel.onFieldsChanged(email, it)
        }
        ForgotPassword(Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.padding(10.dp))
        LoginButton() {
            loginFunction(email, password, viewModel, context)
        }
        Spacer(modifier = Modifier.padding(16.dp))
        DontHaveAccount(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(4.dp))
        RegisterButton { navController.navigate(AppScreens.SignUpView.route); viewModel.cleanFields() }
        Spacer(modifier = Modifier.padding(6.dp))
        SignUpWithGoogleButton(context, token, viewModel)
    }

    loginFlow.value.let {
        if (loginFlag.value == true) {
            Log.d("loginflow", "Ingresando a loginflow")
            when (it) {
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        viewModel.checkIfUserIsAdmin()
                        navController.navigate(HOME_ROUTE) {
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
                else -> {throw IllegalStateException("Error de autenticacion")}
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
private fun EmailField(
    email: String,
    validateEmail: Boolean?,
    validateEmailError: String,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.email),
        showError = !validateEmail!!,
        errorMessage = validateEmailError,
        leadingIconImageVector = Icons.Default.AlternateEmail,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
private fun PasswordField(
    password: String,
    validatePassword: Boolean?,
    validatePasswordError: String,
    isPasswordVissible: Boolean?,
    focusManager: FocusManager,
    onVisibilityChanges: (Boolean) -> Unit,
    onTextFieldChanged: (String) -> Unit

) {
    CustomOutlinedTextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.password),
        showError = !validatePassword!!,
        errorMessage = validatePasswordError,
        isPasswordField = true,
        isPasswordVisible = isPasswordVissible!!,
        onVisibilityChanges = { onVisibilityChanges(it) },
        leadingIconImageVector = Icons.Default.Password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.clearFocus() }
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

private fun loginFunction(email: String, password: String, viewModel: AuthViewModel, context: Context) {
    if (viewModel.validateDataLogin(email, password)) {
        viewModel.login(email, password)
    } else {
        Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun SignUpWithGoogleButton(context: Context, token: String, viewModel: AuthViewModel) {
    var isLoading by remember { mutableStateOf(false) }//para la animacion del circularProgressIndicator
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            isLoading = false
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                val userEmail = account.email!!
                val userName = account.displayName!!
                viewModel.signUpWithGoogle(credential, userEmail, userName, 1)
            } catch (e: ApiException) {
                isLoading = false
                Log.w("TAG", "Google sign in failed", e)
            }
        }

    CustomGoogleButton(isLoading = isLoading) {
        isLoading = true
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)

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