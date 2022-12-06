package com.blessingsoftware.accesibleapp.model.domain

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Suggestion(
    var suggestionName: String = "",
    var suggestionDescription: String = "",
    var suggestionAccessibility: String = "",
    var suggestionDifficulties: String = "",
    var suggestionRate: Int = 0,
    var suggestionType: String = "",
    var suggestionLat: String = "",
    var suggestionLng: String = "",
    var suggestionApproveStatus: Int = 1,//1= No revisado, 2 = Aprobado, 3 = Rechazado
    var suggestionAddedBy: String = "",
    var suggestionReviewedBy: String = "",
    @ServerTimestamp
    val suggestionAddDate: Date? = null,
    @DocumentId
    var suggestionId: String = "",
) {

}