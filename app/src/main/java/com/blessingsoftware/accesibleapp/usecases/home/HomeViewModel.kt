package com.blessingsoftware.accesibleapp.usecases.home


import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(application: Application, private val db: FirestoreRepository): AndroidViewModel(application) {

    var places: MutableLiveData<List<Place>> = MutableLiveData<List<Place>>()
    //Lugar seleccionado en el mapa
    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place> = _selectedPlace
    //Muestra y esconde el bottomBar
    private val _isBottomBarVisible = MutableLiveData<Boolean>()
    val isBottomBarVisible : LiveData<Boolean?> =_isBottomBarVisible

    //El listado de url de imagenes
    private val _imageList = MutableLiveData<ArrayList<Uri>?>(null)
    val imageList: LiveData<ArrayList<Uri>?> = _imageList

    private fun getPlaces(){
        places.value = db.getAllPlaces()
    }

    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
    }

    fun setBottomBarVisible(boolean: Boolean) {
        _isBottomBarVisible.value = boolean
    }

    suspend fun getPlaceImages (uid: String) {
        _imageList.value = db.getImages(uid)
    }

    init {
       getPlaces()
    }

}
/*
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: FirebaseAuthRepository) :
    ViewModel() {


    private var _isNewLocationSelected = MutableLiveData(false)
    var isNewLocationSelected: LiveData<Boolean> = _isNewLocationSelected

    private var _selectedLat = mutableStateOf(0.0)
    var selectedLat: MutableState<Double> = _selectedLat

    private var _selectedlng = mutableStateOf(0.0)
    var selectedlng: MutableState<Double> = _selectedlng

    private var _userCurrentLat = MutableLiveData<Double>()
    var userCurrentLat: LiveData<Double> = _userCurrentLat

    private var _userCurrentLng = MutableLiveData<Double>()
    var userCurrentLng: LiveData<Double> = _userCurrentLng

    //Ubicacion actual del usuario
    //val pickUp = LatLng(userCurrentLat.value, userCurrentLng.value)

    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted : LiveData<Boolean> = _locationPermissionGranted

    init {
        _userCurrentLat.value = 0.0
        _userCurrentLng.value = 0.0
    }

    fun currentUserGeoCoord(latLng: LatLng) {
        _userCurrentLat.value = latLng.latitude
        _userCurrentLng.value = latLng.longitude
        Log.d("Ubicacion actual", "Lat: "+_userCurrentLat.value.toString()+"Lng: "+_userCurrentLng.value.toString())
    }

    fun updateSelectedLocation(status: Boolean) {
        _isNewLocationSelected.value = status
    }

    fun permissionGrand(setGranted: Boolean) {
        _locationPermissionGranted.value = setGranted
    }



}*/


