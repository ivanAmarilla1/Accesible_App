package com.blessingsoftware.accesibleapp.usecases.authentication

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.accesibleapp.R
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.User
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val db: FirestoreRepository
) :
    ViewModel() {

    //TODO Encapsular datos de usuario en un data class
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    //Datos de usuario
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _confirmPassword = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    val email: LiveData<String> = _email
    val password: LiveData<String> = _password
    val confirmPassword: LiveData<String> = _confirmPassword
    val name: LiveData<String> = _name

    //Validaciones
    private val _validateEmail = MutableLiveData<Boolean>()
    private val _validatePassword = MutableLiveData<Boolean>()
    private val _validateName = MutableLiveData<Boolean>()
    private val _validateConfirmPassword = MutableLiveData<Boolean>()
    private val _validatePasswordsEquals = MutableLiveData<Boolean>()
    val validatePasswordsEquals: LiveData<Boolean?> = _validatePasswordsEquals
    val validateConfirmPassword: LiveData<Boolean?> = _validateConfirmPassword
    val validateName: LiveData<Boolean?> = _validateName
    val validatePassword: LiveData<Boolean?> = _validatePassword
    val validateEmail: LiveData<Boolean?> = _validateEmail

    //Visibilidad de la contraseña
    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility: LiveData<Boolean?> = _passwordVisibility
    private val _confirmPasswordVisibility = MutableLiveData<Boolean>()
    val confirmPasswordVisibility: LiveData<Boolean?> = _confirmPasswordVisibility

    //Bandera para entrar a las funciones de login o signUp
    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean> = _flag

    //flujo de login, variable que recupera el usuario al iniciar sesion
    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    //flujo de registro de usuario, recupera al usuario nuevo registrado
    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow


    //Recupera usuario actual del servidor de firebase
    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        //datos de validacion porque si no me da null pointer exception
        _validateEmail.value = true
        _validatePassword.value = true
        _validateName.value = true
        _validateConfirmPassword.value = true
        _validatePasswordsEquals.value = true
        _passwordVisibility.value = false
        _confirmPasswordVisibility.value = false

        //al iniciar la app se recupera la sesion del usuario registrado
        if (repository.currentUser != null) {
            _flag.value = true
            Log.d("currentUser", currentUser?.displayName.toString())
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    //Funcion de inicio de sesion
    fun login(email: String, password: String) = viewModelScope.launch {
        _flag.value = true
        _loginFlow.value = Resource.Loading
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    //funcion de registro con email y password
    fun signUp(name: String, email: String, password: String) = viewModelScope.launch {
        _flag.value = true
        _signUpFlow.value = Resource.Loading
        val result = repository.signUp(name, email, password)
        _signUpFlow.value = result

        //guardar al usuario en la db
        storeFirestoreUser(email, name, "EMAIL")
    }

    //funcion de ingreso con Google
    fun signUpWithGoogle(
        credential: AuthCredential,
        userEmail: String,
        userName: String,
        source: Int
    ) = viewModelScope.launch {
        _flag.value = true
        when (source) {//variable que indica de que pantalla viene la solicitud, para modificar el flow correspondiente
            1 -> {
                _loginFlow.value = Resource.Loading
                val result = repository.signUpWithGoogle(credential)
                _loginFlow.value = result
            }
            2 -> {
                _signUpFlow.value = Resource.Loading
                val result = repository.signUpWithGoogle(credential)
                _signUpFlow.value = result
            }
            else -> {
                Log.d("Error", "error de la fuente de informacion de inicio de sesion")
            }
        }
        //guardar al usuario en la db
        storeFirestoreUser(userEmail, userName, "GOOGLE")
    }

    private suspend fun storeFirestoreUser(email: String, name: String, provider: String) {

        val check = repository.currentUser?.let { db.checkUser(it.uid) } //comprueba si el usuario ya esta almacenado en la db

        if (check != null) {
            Log.d("Check", "Este usuario ya esta registrado en la bd firestore")
        } else {// si no esta almacenado lo guarda
            if (repository.currentUser != null) {
                val uid = repository.currentUser!!.uid
                db.storeUser(uid, email, name, provider)
            }
        }
    }

    //funcion de cierre de sesion
    fun logOut(context: Context) {
        repository.logOut()
        cleanFields()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
    }

    //funcion de onChange(seria basicamente cada vez que se escribe algo en los campos)
    fun onFieldsChanged(email: String, password: String) {
        _flag.value = false
        _email.value = email
        _password.value = password
    }

    //Funcion onChange de la pantalla de registro
    fun onSignUpFieldsChanged(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        _flag.value = false
        _name.value = name
        _email.value = email
        _password.value = password
        _confirmPassword.value = confirmPassword
    }

    //Limpia campos y reinicia variables
    fun cleanFields() {
        _loginFlow.value = null
        _signUpFlow.value = null
        _email.value = ""
        _password.value = ""
        _name.value = ""
        _confirmPassword.value = ""
        _flag.value = false
        _validateEmail.value = true
        _validatePassword.value = true
        _validateName.value = true
        _validateConfirmPassword.value = true
        _validatePasswordsEquals.value = true
        _passwordVisibility.value = false
        _confirmPasswordVisibility.value = false
    }

    //Validacion de email y password para login
    fun validateDataLogin(email: String, password: String): Boolean {
        _validateEmail.value = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _validatePassword.value = password.length >= 6
        return _validateEmail.value!! && _validatePassword.value!!
    }

    //Validacion de email y password para login
    fun validateRegistrationData(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        _validateName.value = name.isNotBlank()
        _validateEmail.value = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _validatePassword.value = password.length >= 6
        _validateConfirmPassword.value = password.length >= 6
        _validatePasswordsEquals.value = password == confirmPassword

        return _validateEmail.value!! && _validatePassword.value!! && _validateName.value!! && _validateConfirmPassword.value!! && _validatePasswordsEquals.value!!
    }


    //Mostrar u ocultar la contraseña
    fun onPasswordVisibilityChanges(passwordVisibility: Boolean) {
        _passwordVisibility.value = passwordVisibility
    }

    fun onConfirmPasswordVisibilityChanges(confirmPasswordVisibility: Boolean) {
        _confirmPasswordVisibility.value = confirmPasswordVisibility
    }

}