package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Suggestion(
    var suggestionName: String = "",
    var suggestionDescription: String = "",
    var suggestionLat: String = "",
    var suggestioneLng: String = "",
    var suggestioneAproveStatus: Boolean = false,
    var suggestioneAddedBy: String = "",
    var suggestioneReviewedBy: String = "",
    @ServerTimestamp
    val timeStamp: Date? = null
) {

}