package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.ui.composables.CustomOutlinedTextField
import com.blessingsoftware.accesibleapp.ui.theme.AccesibleAppTheme
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

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

    //para la autenticacion con google
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)

    //Validacion
    val focusManager = LocalFocusManager.current
    val validateEmail = viewModel.validateEmail.observeAsState()
    val validatePassword = viewModel.validatePassword.observeAsState()
    val validateName = viewModel.validateName.observeAsState()
    val validateConfirmPassword = viewModel.validateConfirmPassword.observeAsState()
    val validatePassordsEquals = viewModel.validatePasswordsEquals.observeAsState()
    val validateEmailError = stringResource(R.string.validate_email)
    val validatePasswordError = stringResource(R.string.validate_password)
    val validateNameError = stringResource(R.string.validate_name)
    val validateConfirmPasswordError = stringResource(R.string.validate_password)
    val validatePasswordsEqualsError = stringResource(R.string.validate_password_match)
    val isPasswordVissible by rememberSaveable { mutableStateOf(false) }
    val isConfirmPasswordVissible by rememberSaveable { mutableStateOf(false) }


    Column(modifier.verticalScroll(rememberScrollState())) {
        SignUpHeader(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(20.dp))
        SignUpNameField(name, validateName.value, validateNameError, focusManager) {
            viewModel.onSignUpFieldsChanged(
                it,
                email,
                password,
                confirmPassword
            )
        }
        //Spacer(modifier = Modifier.padding(10.dp))
        SignUpEmailField(email, validateEmail.value, validateEmailError, focusManager) {
            viewModel.onSignUpFieldsChanged(
                name,
                it,
                password,
                confirmPassword
            )
        }
        //Spacer(modifier = Modifier.padding(10.dp))
        SignUpPasswordField(
            password,
            validatePassword.value,
            validatePasswordError,
            isPasswordVissible,
            focusManager
        ) {
            viewModel.onSignUpFieldsChanged(
                name,
                email,
                it,
                confirmPassword
            )
        }
        //Spacer(modifier = Modifier.padding(10.dp))
        SignUpConfirmPasswordField(
            confirmPassword,
            validateConfirmPassword.value,
            validatePassordsEquals.value,
            validateConfirmPasswordError,
            validatePasswordsEqualsError,
            isConfirmPasswordVissible,
            focusManager
        ) {
            viewModel.onSignUpFieldsChanged(
                name,
                email,
                password,
                it
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        SignUpButton {
            registerFunction(name, email, password, confirmPassword, viewModel, context)
        }
        Spacer(modifier = Modifier.padding(7.dp))
        AlreadyHaveAnAccount(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(5.dp))
        OrDivider()
        Spacer(modifier = Modifier.padding(7.dp))
        SignUpWithGoogleButton(context, token, viewModel)
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
private fun SignUpNameField(
    name: String,
    validateName: Boolean?,
    validateNameError: String,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextField(
        value = name,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.name),
        showError = !validateName!!,
        errorMessage = validateNameError,
        leadingIconImageVector = Icons.Default.PermIdentity,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
private fun SignUpEmailField(
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
private fun SignUpPasswordField(
    password: String,
    validatePassword: Boolean?,
    validatePasswordError: String,
    isPasswordVissible: Boolean,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.password),
        showError = !validatePassword!!,
        errorMessage = validatePasswordError,
        isPasswordField = true,
        isPasswordVisible = isPasswordVissible,
        //onVisibilityChanges = { isPasswordVissible = it },
        leadingIconImageVector = Icons.Default.Password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
private fun SignUpConfirmPasswordField(
    confirmPassword: String,
    validateConfirmPassword: Boolean?,
    validatePaswordsEquals: Boolean?,
    validateConfirmPasswordError: String,
    validatePaswordsEqualsError: String,
    isConfirmPasswordVissible: Boolean,
    focusManager: FocusManager,
    onTextFieldChanged: (String) -> Unit
) {
    CustomOutlinedTextField(
        value = confirmPassword,
        onValueChange = { onTextFieldChanged(it) },
        label = stringResource(R.string.confirm_password),
        showError = !validateConfirmPassword!! || !validatePaswordsEquals!!,
        errorMessage = if (!validateConfirmPassword) validateConfirmPasswordError else validatePaswordsEqualsError,
        isPasswordField = true,
        isPasswordVisible = isConfirmPasswordVissible,
        //onVisibilityChanges = { isPasswordVissible = it },
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

fun registerFunction(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    viewModel: AuthViewModel,
    context: Context
) {
    if (viewModel.validateRegistrationData(name, email, password, confirmPassword)) {
        viewModel.signUp(name, email, password)
    } else {
        Toast.makeText(context, "Corriga los errores en los campos", Toast.LENGTH_LONG)
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
            startIndent = 8.dp,
            thickness = 1.dp,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .width(165.dp)
        )
        Text("  " + stringResource(R.string.or), color = MaterialTheme.colors.secondary)
        Divider(startIndent = 8.dp, thickness = 1.dp, color = MaterialTheme.colors.secondary)
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
                viewModel.signUpWithGoogle(credential, 2)
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
        Text(
            text = " " + stringResource(R.string.google_signin),
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun SignUpBackHandler(viewModel: AuthViewModel, navController: NavController) {
    BackHandler(enabled = true, onBack = {
        viewModel.cleanFields()
        navController.navigate(AppScreens.LoginView.route) {
            popUpTo(AppScreens.LoginView.route) { inclusive = true }
        }

        Log.d("BackHandler", "Boton atras")
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