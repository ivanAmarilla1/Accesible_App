package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _scrollEnabled = MutableLiveData<Boolean>()
    val scrollEnabled : LiveData<Boolean?> =_scrollEnabled


    private val _suggestion = MutableLiveData<Suggestion>()
    val suggestion: LiveData<Suggestion>
        get() = _suggestion

    init {
        _validateName.value = true
        _validateDescription.value = true
        _scrollEnabled.value = true
    }

    fun onFieldsChanged(name: String, description: String) {
        _name.value = name
        _description.value = description
    }

    fun validateDataMakeSuggestion(name: String, description: String): Boolean {
        _validateName.value = name.isNotEmpty()
        _validateDescription.value = description.isNotEmpty()
        return _validateName.value!! && _validateDescription.value!!
    }

    fun makeSuggestion(name: String, description: String) {
        Log.d("Suggestion", "Sugerencia enviada")
    }

    fun cleanSuggestionFields() {
        _name.value = ""
        _description.value = ""
        _validateName.value = false
        _validateDescription.value = false

    }

}