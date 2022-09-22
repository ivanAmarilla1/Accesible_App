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
class AuthViewModel @Inject constructor(private val repository: FirebaseAuthRepository) :
    ViewModel() {

    //variables privadas que solo se puede modificar desde el viewmodel
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _confirmPassword = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _flag = MutableLiveData<Boolean>()

    //variable que se conecta con el view para capturar la interacciones del usuario
    val email: LiveData<String> = _email
    val password: LiveData<String> = _password
    val confirmPassword: LiveData<String> = _confirmPassword
    val name: LiveData<String> = _name
    val flag: LiveData<Boolean> = _flag


    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val googleSignIn: GoogleSignInClient
        get() {
            TODO()
        }


    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser != null) {
            _flag.value = true
            Log.d("currentUser", currentUser?.displayName.toString())
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    //Funcion de inicio de sesion
    fun login(email: String, password: String) = viewModelScope.launch {
        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                _flag.value = true
                _loginFlow.value = Resource.Loading
                val result = repository.login(email, password)
                _loginFlow.value = result
            } else {
                //TODO mensaje de password invalido
            }
        } else {
            //TODO mensaje de email invalido
        }
    }

    fun signUp(name: String, email: String, password: String) = viewModelScope.launch {
        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                _flag.value = true
                _signUpFlow.value = Resource.Loading
                val result = repository.signUp(name, email, password)
                _signUpFlow.value = result
            } else {
                //TODO mensaje de password invalido
            }
        } else {
            //TODO mensaje de email invalido
        }
    }

    fun signUpWithGoogle(credential: AuthCredential) = viewModelScope.launch {
        _flag.value = true
        _loginFlow.value = Resource.Loading
        val result = repository.signUpWithGoogle(credential)
        _loginFlow.value = result

    }

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

    //Limpiador de campos
    fun cleanFields() {
        _loginFlow.value = null
        _signUpFlow.value = null
        _email.value = ""
        _password.value = ""
        _name.value = ""
        _confirmPassword.value = ""
        _flag.value = false
    }

    //Validacion de email y password
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 6

}