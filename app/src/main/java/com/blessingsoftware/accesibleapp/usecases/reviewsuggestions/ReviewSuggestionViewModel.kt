package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class ReviewSuggestionViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val db: FirestoreRepository
) :
    ViewModel() {
    var suggestions: MutableLiveData<List<Suggestion>> = MutableLiveData<List<Suggestion>>()

    private val _selectedSuggestion = MutableLiveData<Suggestion>()
    val selectedSuggestion: LiveData<Suggestion> = _selectedSuggestion
    //Para el callback de traer todas las sugerencias
    private val _getSuggestionFlow = MutableStateFlow<Resource<String>?>(null)
    val getSuggestionFlow: StateFlow<Resource<String>?> = _getSuggestionFlow
    //Para el callback de aproba/rechazar sugerencia
    private val _approveSuggestionFlow = MutableStateFlow<Resource<String>?>(null)
    val approveSuggestionFlow: StateFlow<Resource<String>?> = _approveSuggestionFlow
    //Bandera
    private val _approveSuggestionFlag = MutableLiveData<Boolean>()
    val approveSuggestionFlag: LiveData<Boolean> = _approveSuggestionFlag
    //Mensaje para el toast
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    init {
        _getSuggestionFlow.value = Resource.Loading
    }

    fun setSelectedSuggestion(suggestion: Suggestion) {
        _selectedSuggestion.value = suggestion
    }


    suspend fun getSuggestions() {
        _getSuggestionFlow.value = Resource.Loading
        //Log.d("Valor", "${_getSuggestionFlow.value}")
        suggestions.value = db.getAllSuggestions()
        _getSuggestionFlow.value = Resource.Success("Success")
    }
    //TODO Evitar que se puedan tocar de nuevo los botones de aceptar o rechazar cuando ya se acepto/rechazo la solicitud
    suspend fun approveSuggestion(suggestion: Suggestion) {
        suggestion.suggestionApproveStatus = 2
        val place = Place(
            suggestion.suggestionName,
            suggestion.suggestionDescription,
            suggestion.suggestionLat,
            suggestion.suggestionLng,
            suggestion.suggestionAddedBy,
            repository.currentUser!!.uid,
            suggestion.suggestionRate,
            suggestion.suggestionType
        )
        _message.value = "Sugerencia aprobada correctamente"
        _approveSuggestionFlag.value = true
        _approveSuggestionFlow.value = Resource.Loading
        val storePlace = db.storePlace(place)
        if (storePlace == Resource.Success("Success")){
            val updateSuggestion = db.updateSuggestion(suggestion = suggestion, repository.currentUser!!.uid)
            _approveSuggestionFlow.value = updateSuggestion
            Log.d("Hola", _approveSuggestionFlow.value.toString())
        } else {
            _approveSuggestionFlow.value = storePlace
        }


    }

    suspend fun declineSuggestion(suggestion: Suggestion) {
        _message.value = "Sugerencia rechazada correctamente"
        _approveSuggestionFlag.value = true
        _approveSuggestionFlow.value = Resource.Loading
        suggestion.suggestionApproveStatus = 3
        val updateSuggestion = db.updateSuggestion(suggestion = suggestion, repository.currentUser!!.uid)
        _approveSuggestionFlow.value = updateSuggestion
    }

    fun clean(){
        _approveSuggestionFlag.value = false
        _message.value = ""
    }


}
