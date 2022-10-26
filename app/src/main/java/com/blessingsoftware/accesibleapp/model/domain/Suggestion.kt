package com.blessingsoftware.accesibleapp.model.domain

data class Suggestion(
    var suggestionName: String = "",
    var suggestionDescription: String = "",
    var suggestionLat: String = "",
    var suggestioneLng: String = "",
    var suggestioneAproveStatus: Boolean = false,
    var suggestioneUserId: String = "",
    var suggestioneReviewedBy: String = "",
    //var id: String = ""
) {

}