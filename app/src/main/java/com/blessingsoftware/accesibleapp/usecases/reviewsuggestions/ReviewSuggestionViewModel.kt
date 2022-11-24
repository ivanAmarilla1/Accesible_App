package com.blessingsoftware.accesibleapp.usecases.reviewsuggestions

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
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

    //Variable para saber si la sugerencia fue eliminada
    //Bandera
    private val _isSuggestionEliminated = MutableLiveData<Boolean>()
    val isSuggestionEliminated: LiveData<Boolean> = _isSuggestionEliminated

    //Mensaje para el toast
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    //Dialg de probrar o rechazar
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    //Para el drop down menu
    private val _suggestionViewType = MutableLiveData<Int>()
    val suggestionViewType: LiveData<Int> = _suggestionViewType

    //El listado de url de imagenes
    private val _imageList = MutableLiveData<ArrayList<Uri>?>(null)
    val imageList: LiveData<ArrayList<Uri>?> = _imageList



    init {
        _getSuggestionFlow.value = Resource.Loading
        _isSuggestionEliminated.value = false
    }

    fun setSelectedSuggestion(suggestion: Suggestion) {
        _selectedSuggestion.value = suggestion
        _showDialog.value = false
    }


    suspend fun getSuggestions(suggestionApproveStatus: Int) {
        Log.d("Dentro del viewmodel", suggestionApproveStatus.toString())
        _getSuggestionFlow.value = Resource.Loading
        suggestions.value = db.getSuggestions("suggestionApproveStatus", suggestionApproveStatus)
        _getSuggestionFlow.value = Resource.Success("Success")
    }

    private suspend fun checkIfUserIsAdminSuggestion(): Boolean {
        val user = repository.currentUser?.let { db.checkUser(it.uid) }
        return user?.admin ?: false
    }

    suspend fun approveSuggestion(suggestion: Suggestion) {
        setShowDialogFalse()
        _approveSuggestionFlow.value = Resource.Loading
        if (checkIfUserIsAdminSuggestion()) {
            if (suggestion.suggestionApproveStatus == 1) {
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
                val storePlace = db.storePlace(place)
                if (storePlace == Resource.Success("Success")) {
                    val updateSuggestion =
                        db.updateSuggestion(suggestion = suggestion, repository.currentUser!!.uid)
                    _approveSuggestionFlow.value = updateSuggestion
                    Log.d("Hola", _approveSuggestionFlow.value.toString())
                } else {
                    _approveSuggestionFlow.value = storePlace
                }
            }
        } else {
            _approveSuggestionFlag.value = true
            _approveSuggestionFlow.value =
                Resource.Failure(exception = Exception("Usted no es un administrador"))
        }
    }

    suspend fun getSuggestionImages (uid: String) {
        _imageList.value = db.getImages(uid)
    }

    suspend fun declineSuggestion(suggestion: Suggestion) {
        setShowDialogFalse()
        _approveSuggestionFlow.value = Resource.Loading
        if (checkIfUserIsAdminSuggestion()) {
            if (suggestion.suggestionApproveStatus == 1) {
                _message.value = "Sugerencia rechazada correctamente"
                _approveSuggestionFlag.value = true
                suggestion.suggestionApproveStatus = 3
                val updateSuggestion =
                    db.updateSuggestion(suggestion = suggestion, repository.currentUser!!.uid)
                _approveSuggestionFlow.value = updateSuggestion
            }
        } else {
            _approveSuggestionFlag.value = true
            _approveSuggestionFlow.value =
                Resource.Failure(exception = Exception("Usted no es un administrador"))
        }
    }

    suspend fun deleteMySuggestion(suggestion: Suggestion) {
        setShowDialogFalse()
        _approveSuggestionFlow.value = Resource.Loading
        if (checkIfUserIsAdminSuggestion()) {
            if (suggestion.suggestionApproveStatus != 1) {
                _isSuggestionEliminated.value = true
                _message.value = "Sugerencia eliminada correctamente"
                _approveSuggestionFlag.value = true
                val deleteSuggestion =
                    db.deleteSuggestion(suggestion.suggestionId)
                _approveSuggestionFlow.value = deleteSuggestion
            }
        } else {
            _approveSuggestionFlag.value = true
            _approveSuggestionFlow.value =
                Resource.Failure(exception = Exception("Usted no es un administrador"))
        }
    }

    fun clean() {
        _approveSuggestionFlag.value = false
        _approveSuggestionFlow.value = null
        _message.value = ""
        _showDialog.value = false
        _isSuggestionEliminated.value = false
    }

    fun setShowDialogTrue() {
        _showDialog.value = true
    }

    fun setShowDialogFalse() {
        _showDialog.value = false
    }

    fun setSuggestionViewType(type: Int) {
        _suggestionViewType.value = type
    }
}
