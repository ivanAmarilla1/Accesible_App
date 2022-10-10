package com.blessingsoftware.accesibleapp.model.domain


data class User(
    var email: String,
    var password: String,
    var confirmPassword: String,
    var name: String
    ) {

    constructor(): this("","","","")
}
