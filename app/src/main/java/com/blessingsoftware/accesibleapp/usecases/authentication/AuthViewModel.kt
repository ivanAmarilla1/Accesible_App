package com.blessingsoftware.accesibleapp.usecases.authentication

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
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
    private val _flag = MutableLiveData<Boolean>()

    //variable que se conecta con el view para capturar la interacciones del usuario
    val email: LiveData<String> = _email
    val password: LiveData<String> = _password
    val flag: LiveData<Boolean> = _flag


    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser != null) {
            _flag.value = true
            Log.d("currentUser",currentUser?.displayName.toString())
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
        _signUpFlow.value = Resource.Loading
        val result = repository.signUp(name, email, password)
        _signUpFlow.value = result
    }

    fun logOut() {
        repository.logOut()
        _loginFlow.value = null
        _signUpFlow.value = null
    }

    //funcion de onChange(seria basicamente cada vez que se escribe algo en los campos)
    fun onFieldsChanged(email: String, password: String) {
        _flag.value = false
        _email.value = email
        _password.value = password
    }

    //Validacion de email y password
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length > 6

}