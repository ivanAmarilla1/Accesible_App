package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.model.domain.LocationLiveData
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
    application: Application,
    private val db: FirestoreRepository
) :
    AndroidViewModel(application) {
    //Ubicacion del usuario en tiempo real
    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }
    //variables Livedata para el almacenar los datos del formulario
    private val _name = MutableLiveData<String>()
    val name : LiveData<String> =_name
    private val _description = MutableLiveData<String>()
    val description : LiveData<String> =_description
    private val _markerLocation = MutableLiveData<LatLng>()
    val markerLocation : LiveData<LatLng> =_markerLocation
    private val _rating = MutableLiveData<Int>()
    val rating: LiveData<Int> = _rating
    private val _placeType = MutableLiveData<String>()
    val placeType: LiveData<String> = _placeType
    //validacion
    private val _validateName = MutableLiveData<Boolean>()
    val validateName : LiveData<Boolean?> =_validateName
    private val _validateDescription = MutableLiveData<Boolean>()
    val validateDescription : LiveData<Boolean?> =_validateDescription
    private val _validateType = MutableLiveData<Boolean>()
    val validateType : LiveData<Boolean?> =_validateType
    private val _validateRate = MutableLiveData<Boolean>()
    val validateRate : LiveData<Boolean?> =_validateRate
    //Variable de control para los callbacks del servidor
    private val _suggestionFlow = MutableStateFlow<Resource<String>?>(null)
    val suggestionFlow: StateFlow<Resource<String>?> = _suggestionFlow
    //Bandera para entrar a las funciones de de los callbacks
    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean> = _flag
    //Control de permisos (creo que no se usa ahora)
    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted : LiveData<Boolean> = _locationPermissionGranted
    fun permissionGrand(setGranted: Boolean) {
        _locationPermissionGranted.value = setGranted
    }

    /*
    private val _suggestion = MutableLiveData<Suggestion>()
    val suggestion: LiveData<Suggestion>
        get() = _suggestion*/

    //al inicio del viewmodel
    init {
        //seteo de variables para evitar nullPointerExeption
        _validateName.value = true
        _validateDescription.value = true
        _validateType.value = true
        _validateRate.value = true
        //Se llama a la ubicacion del usuario
        startLocationUpdates()
    }

    //Actualizacion de campos del formulario con el Livedata
    fun onFieldsChanged(name: String, description: String) {
        _flag.value = false
        _name.value = name
        _description.value = description
    }

    //validacion de que los campos no esten vacios
    fun validateDataMakeSuggestion(name: String, description: String, placeType: String, rate: Int): Boolean {
        _validateName.value = name.isNotEmpty()
        _validateDescription.value = description.isNotEmpty()
        _validateType.value = placeType != "Seleccione"
        _validateRate.value = (rate in 1..5)
        return _validateName.value!! && _validateDescription.value!! && _validateType.value!! && _validateRate.value!!
    }

    //Almacenar la sugerencia en la bd firestore
    suspend fun makeSuggestion(name: String, description: String, rate: Int, placetype: String, marker: LatLng, user: String) {
        val suggestion = Suggestion(name,description, rate, placetype, marker.latitude.toString(), marker.longitude.toString(), false, user)
        _flag.value = true
        _suggestionFlow.value = Resource.Loading
        val result = db.storeSuggestion(suggestion)
        _suggestionFlow.value = result
    }
    //Limpiar campos y reiniciar variables
    fun cleanSuggestionFields() {
        _name.value = ""
        _description.value = ""
        _rating.value = 0
        _placeType.value = "Seleccione"
        _validateName.value = true
        _validateDescription.value = true
        _validateType.value = true
        _validateRate.value = true
        _flag.value = false

    }
    //Colocar el marcador en el mapa con el Livedata
    fun setMarker(markerLocation: LatLng) {
        _markerLocation.value = markerLocation
    }

    fun setInitialMarker(userLocation: LatLng) {
        _markerLocation.value = userLocation
    }

    fun setRating(rate: Int) {
        _rating.value = rate
        Log.d("rate", _rating.value.toString())
    }

    fun setPlaceType(type: String) {
        _placeType.value = type
    }

}