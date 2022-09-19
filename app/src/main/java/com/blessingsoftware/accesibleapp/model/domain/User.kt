package com.blessingsoftware.accesibleapp.model.domain


data class User(
    val email: String,
    val password: String
    ) {

    constructor(): this("","")
}