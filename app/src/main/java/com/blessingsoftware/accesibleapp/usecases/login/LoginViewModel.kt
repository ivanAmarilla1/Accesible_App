package com.blessingsoftware.accesibleapp.usecases.login

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthenication
import com.blessingsoftware.accesibleapp.usecases.navigation.AppScreens
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    //variables privadas que solo se puede modificar desde el viewmodel
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _buttonEnabler = MutableLiveData<Boolean>()
    private val _isLoading = MutableLiveData<Boolean>()
    //variable que se conecta con el view para capturar la interacciones del usuario
    val email : LiveData<String> = _email
    val password : LiveData<String> = _password
    val buttonEnabler : LiveData<Boolean> = _buttonEnabler
    val isLoading : LiveData<Boolean> = _isLoading



    //funcion de onChange(seria basicamente cada vez que se escribe algo en los campos)
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _buttonEnabler.value = isValidEmail(email) && isValidPassword(password)
    }
    //Validacion de email y password
    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length > 6

    //Activa/desactiva el boton de inicias sesion
    suspend fun onLoginSelected(navController: NavController) {
        Log.d("onLoginSelected", "Se llamo al login")
        var authUser: FirebaseUser? = null

        authUser = FirebaseAuthenication.signInUser(_email.value, _password.value )

        if (authUser != null) {
            navController.navigate(AppScreens.HomeView.route)
            authUser!!.email?.let { Log.d("User", it.trim()) }
        }else {
            Log.d("User", "Error")
        }

    }

    fun registerUser(navController: NavController) {
        navController.navigate(AppScreens.HomeView.route)
    }


}