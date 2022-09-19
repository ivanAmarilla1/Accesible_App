package com.blessingsoftware.accesibleapp.provider.firebase

import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


object FirebaseAuthenication {

    //Propiedades


    //Servicios

    // ...
// Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()
    fun signInUser(
        email: String?,
        password: String?,
    ): FirebaseUser? {
        var user: FirebaseUser? = null
        if (email != null && password != null) {
            if (email.isNotEmpty() && password.isNotEmpty()) {//Se asegura de que los campos de usuario y contraseña no esten vacios
                auth.signInWithEmailAndPassword(//llama a la funcion de inicio de sesion de Firebase pasando el correo y contraseña
                        email,
                        password
                    ).addOnCompleteListener() { task ->
                        if (task.isSuccessful) {//esto se va a ejecutar al completar la tarea de creado de usuario
                            Log.d("AUTH", "Sign up succesfull!")
                            user = auth.currentUser
                        } else {//esto si hay algun error
                            Log.d("AUTH", "Failed: ${task.exception}")
                            user = null
                        }
                    }
            }
        }
        Log.d("AUTH", "Ya se hizo el retorno")
        return user
    }


}