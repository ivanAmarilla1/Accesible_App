package com.blessingsoftware.accesibleapp.usecases.home


import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.LocationLiveData
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class HomeViewModel (application: Application): AndroidViewModel(application) {
    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted : LiveData<Boolean> = _locationPermissionGranted
    fun permissionGrand(setGranted: Boolean) {
        _locationPermissionGranted.value = setGranted
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


