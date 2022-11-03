package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ReviewSuggestionViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val db: FirestoreRepository
) :
    ViewModel() {
    var suggestions: MutableLiveData<List<Suggestion>> = MutableLiveData<List<Suggestion>>()

    suspend fun getSuggestions() {
        suggestions.value = db.getAllSuggestions()
    }

}
