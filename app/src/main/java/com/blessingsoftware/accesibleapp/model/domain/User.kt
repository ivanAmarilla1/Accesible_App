package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class User(
    var admin: Boolean = false,
    var name: String = "",
    var email: String = "",
    var provider: String = "",
    @ServerTimestamp
    var added: Date? = null,
    @DocumentId
    var userId: String = "",
) {

}
