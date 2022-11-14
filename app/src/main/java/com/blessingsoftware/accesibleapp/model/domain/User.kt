package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp


data class User(
    var admin: Boolean,
    var name: String,
    var provider: String,
    @ServerTimestamp
    var added: String,
    @DocumentId
    var userId: String = "",

    ) {

    constructor(): this(false,"","","")

}
