package com.blessingsoftware.accesibleapp.model.domain


data class User(
    val email: String,
    val password: String
    ) {

    constructor(): this("","")
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    class AuthError(val message: String? = null) : AuthState()
}