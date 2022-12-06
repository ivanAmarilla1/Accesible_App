package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Place(
    var placeName: String = "",
    var placeDescription: String = "",
    var placeAccessibility: String = "",
    var placeDifficulties: String = "",
    var placeLat: String = "",
    var placeLng: String = "",
    var placeAddedBy: String = "",
    var placeReviewedBy: String = "",
    var placeRate: Int = 0,
    var placeType: String = "",
    var placeImages: String = "",
    @ServerTimestamp
    val placeAddDate: Date? = null,
    @DocumentId
    var placeId: String = "",
) {

}