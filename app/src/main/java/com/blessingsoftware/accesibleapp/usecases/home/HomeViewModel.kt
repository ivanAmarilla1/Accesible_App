package com.blessingsoftware.accesibleapp.usecases.home


import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val db: FirestoreRepository
) : AndroidViewModel(application) {

    //TODO DEL VIEW DEL MAPA
    var places: MutableLiveData<List<Place>> = MutableLiveData<List<Place>>()

    //Lugar seleccionado en el mapa
    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place> = _selectedPlace


    //Muestra y esconde el bottomBar
    private val _isBottomBarVisible = MutableLiveData<Boolean>()
    val isBottomBarVisible: LiveData<Boolean?> = _isBottomBarVisible

    //El listado de url de imagenes
    private val _imageList = MutableLiveData<ArrayList<Uri>?>(null)
    val imageList: LiveData<ArrayList<Uri>?> = _imageList

    fun cleanHome() {
        _isBottomBarVisible.value = false
        _imageList.value = null
        _showRatingDialog.value = false
    }

    private fun getPlaces() {
        places.value = db.getAllPlaces()
    }

    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
    }


    fun setBottomBarVisible(boolean: Boolean) {
        _isBottomBarVisible.value = boolean
    }

    suspend fun getPlaceImages(uid: String) {
        _imageList.value = db.getImages(uid)
    }

    fun setImageListEmpty() {
        _imageList.value = arrayListOf()
    }


    //TODO DE LA OPCION DE BUSQUEDA

    //Para el callback de traer lugares
    private val _getPlacesFlow = MutableStateFlow<Resource<String>?>(null)
    val getPlacesFlow: StateFlow<Resource<String>?> = _getPlacesFlow

    //Lugares de la funcion de busqueda
    var searchedPlaces: MutableLiveData<List<Place>> = MutableLiveData<List<Place>>()

    //Para el drop down menu
    private val _selectedPlaceType = MutableLiveData<String>()
    val selectedPlaceType: LiveData<String> = _selectedPlaceType

    private val _selectedSearchedPlace = MutableLiveData<Place>()
    val selectedSearchedPlace: LiveData<Place> = _selectedSearchedPlace

    //El listado de url de imagenes
    private val _searchImageList = MutableLiveData<ArrayList<Uri>?>(null)
    val searchImageList: LiveData<ArrayList<Uri>?> = _searchImageList


    fun setSelectedPlaceType(placeType: String) {
        _selectedPlaceType.value = placeType
    }

    fun setSelectedSearchedPlace(suggestion: Place) {
        _selectedSearchedPlace.value = suggestion
    }

    suspend fun getSeletedPlaces(placeType: String) {
        //Log.d("Dentro del viewmodel", placeType)
        _getPlacesFlow.value = Resource.Loading
        searchedPlaces.value = db.getPlaces("placeType", placeType)
        _getPlacesFlow.value = Resource.Success("Success")
    }

    fun cleanImages() {
        _searchImageList.value = null
    }


    //TODO Calificar sitios
    //Para el callback de agregar calificacion
    private val _addRateFlow = MutableStateFlow<Resource<String>?>(null)
    val addRateFlow: StateFlow<Resource<String>?> = _addRateFlow

    //Bandera
    private val _addRateFlag = MutableLiveData<Boolean>()
    val addRateFlag: LiveData<Boolean> = _addRateFlag

    //Mostrar o no el dialogo de calificacion
    private val _showRatingDialog = MutableLiveData<Boolean>()
    val showRatingDialog: LiveData<Boolean> = _showRatingDialog

    //para validar que se ha ingresado la calificacion cuando se quiere enviar
    private val _validatePlaceRate = MutableLiveData<Boolean>()
    val validatePlaceRate: LiveData<Boolean?> = _validatePlaceRate

    //el rating del usuario
    private val _userRating = MutableLiveData<Int>()
    val userRating: LiveData<Int> = _userRating

    suspend fun addPlaceRate(selectedPlace: Place?, rate: Int, currentUser: FirebaseUser?) {
        _addRateFlow.value = Resource.Loading
        _addRateFlag.value = true
        if (selectedPlace != null) {
            if (currentUser != null) {
                val checkUserPlaceRate =
                    db.checkUserPlaceRate(currentUser.uid, selectedPlace.placeId)
                when (val key = checkUserPlaceRate.keys.first()) {
                    Resource.Success("Exist") -> {

                        //Calculo de la calificacion cuando ya existia una anterior del mismo usuario
                        val userRate= checkUserPlaceRate.get(key)
                        var actualRate = selectedPlace.placeRate
                        if (userRate != null) {
                            actualRate = (selectedPlace.placeRate*2) - userRate
                        }

                        db.addUserPlaceRate(currentUser.uid, selectedPlace.placeId, rate.toDouble())
                        val addPlaceRate = db.addPlaceRate(
                            selectedPlace.placeId,
                            actualRate,
                            selectedPlace.placeNumberOfRaters,
                            rate,
                            0
                        )
                        _addRateFlow.value = addPlaceRate
                    }
                    Resource.Success("DontExist") -> {
                        db.addUserPlaceRate(currentUser.uid, selectedPlace.placeId, rate.toDouble())
                        val addPlaceRate = db.addPlaceRate(
                            selectedPlace.placeId,
                            selectedPlace.placeRate,
                            selectedPlace.placeNumberOfRaters,
                            rate,
                            1
                        )
                        _addRateFlow.value = addPlaceRate
                    }
                    else -> {
                        _addRateFlow.value = key
                    }
                }

            }
        }
    }

    fun setShowDialogTrue() {
        _showRatingDialog.value = true
    }

    fun setShowDialogFalse() {
        _showRatingDialog.value = false
        _userRating.value = 0
        _validatePlaceRate.value = true
    }

    fun validatePlaceRate(rate: Int): Boolean {
        _validatePlaceRate.value = (rate in 1..5)
        return _validatePlaceRate.value!!
    }

    fun setUserRating(rate: Int) {
        _userRating.value = rate
    }

    fun cleanRates() {
        _addRateFlow.value = null
        _addRateFlag.value = false
        _userRating.value = 0
        setShowDialogFalse()
    }


    init {
        getPlaces()
        _getPlacesFlow.value = Resource.Loading
        _validatePlaceRate.value = true
    }
}