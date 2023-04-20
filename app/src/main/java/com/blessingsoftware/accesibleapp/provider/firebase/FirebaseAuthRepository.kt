package com.blessingsoftware.accesibleapp.provider.firebase

import android.content.Context
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository{
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>
    suspend fun signUpWithGoogle(credential: AuthCredential): Resource<FirebaseUser>
    suspend fun recoverPassword (email: String, context: Context): String
    fun logOut()
}
