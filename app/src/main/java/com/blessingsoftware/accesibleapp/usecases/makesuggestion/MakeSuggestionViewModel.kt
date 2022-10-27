package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MakeSuggestionViewModel @Inject constructor(
    private val repository: FirebaseAuthRepository,
    private val db: FirestoreRepository
) :
    ViewModel() {

    private val _name = MutableLiveData<String>()
    val name : LiveData<String> =_name
    private val _description = MutableLiveData<String>()
    val description : LiveData<String> =_description

    private val _validateName = MutableLiveData<Boolean>()
    val validateName : LiveData<Boolean?> =_validateName
    private val _validateDescription = MutableLiveData<Boolean>()
    val validateDescription : LiveData<Boolean?> =_validateDescription

    private val _markerLocation = MutableLiveData<LatLng>()
    val markerLocation : LiveData<LatLng> =_markerLocation

    private val _suggestionFlow = MutableStateFlow<Resource<String>?>(null)
    val suggestionFlow: StateFlow<Resource<String>?> = _suggestionFlow

    //Bandera para entrar a las funciones de login o signUp
    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean> = _flag

    //private val _scrollEnabled = MutableLiveData<Boolean>()
    //val scrollEnabled : LiveData<Boolean?> =_scrollEnabled


    private val _suggestion = MutableLiveData<Suggestion>()
    val suggestion: LiveData<Suggestion>
        get() = _suggestion

    init {
        _validateName.value = true
        _validateDescription.value = true
        //_scrollEnabled.value = true
    }

    fun onFieldsChanged(name: String, description: String) {
        _flag.value = false
        _name.value = name
        _description.value = description
    }

    fun validateDataMakeSuggestion(name: String, description: String): Boolean {
        _validateName.value = name.isNotEmpty()
        _validateDescription.value = description.isNotEmpty()
        return _validateName.value!! && _validateDescription.value!!
    }

    fun makeSuggestion(name: String, description: String, marker: LatLng, user: String) {
        _flag.value = true
        _suggestionFlow.value = Resource.Loading
        val result = db.storeSuggestion(name, description, marker.latitude.toString(), marker.longitude.toString(), user)
        _suggestionFlow.value = result
    }

    fun cleanSuggestionFields() {
        _name.value = ""
        _description.value = ""
        _validateName.value = true
        _validateDescription.value = true
        _flag.value = false

    }

    fun setMarker(markerLocation: LatLng) {
        _markerLocation.value = markerLocation
    }

}