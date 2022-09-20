package com.blessingsoftware.accesibleapp.provider.firebase

import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.AuthState
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface FirebaseAuthRepository{
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logOut()
}
