package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.ServerTimestamp


data class User(
    var email: String,
    var password: String,
    var confirmPassword: String,
    @ServerTimestamp
    var name: String,

    ) {

    constructor(): this("","","","")

}
