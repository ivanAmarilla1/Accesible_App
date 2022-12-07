package com.blessingsoftware.accesibleapp.usecases.makesuggestion

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blessingsoftware.accesibleapp.model.domain.*
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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
    val name: LiveData<String> = _name
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _accessibility = MutableLiveData<String>()
    val accessibility: LiveData<String> = _accessibility
    private val _difficulties = MutableLiveData<String>()
    val difficulties: LiveData<String> = _difficulties
    private val _markerLocation = MutableLiveData<LatLng>()
    val markerLocation: LiveData<LatLng> = _markerLocation
    private val _rating = MutableLiveData<Int>()
    val rating: LiveData<Int> = _rating
    private val _placeType = MutableLiveData<String>()
    val placeType: LiveData<String> = _placeType

    //validacion
    private val _validateName = MutableLiveData<Boolean>()
    val validateName: LiveData<Boolean?> = _validateName
    private val _validateDescription = MutableLiveData<Boolean>()
    val validateDescription: LiveData<Boolean?> = _validateDescription
    private val _validateAccessibility = MutableLiveData<Boolean>()
    val validateAccessibility: LiveData<Boolean?> = _validateAccessibility
    private val _validateDifficulties = MutableLiveData<Boolean>()
    val validateDifficulties: LiveData<Boolean?> = _validateDifficulties

    private val _validateType = MutableLiveData<Boolean>()
    val validateType: LiveData<Boolean?> = _validateType
    private val _validateRate = MutableLiveData<Boolean>()
    val validateRate: LiveData<Boolean?> = _validateRate

    //Variable de control para los callbacks del servidor
    private val _suggestionFlow = MutableStateFlow<Resource<String>?>(null)
    val suggestionFlow: StateFlow<Resource<String>?> = _suggestionFlow

    //Variable de control para saber si el GPS esta encendido o no
    private val _isGPSOn = MutableLiveData<Boolean>()
    val isGPSOn: LiveData<Boolean?> = _isGPSOn

    //Bandera para entrar a las funciones de de los callbacks
    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean> = _flag

    //Dialg de probrar o rechazar
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    //Imagen
    //private val _imageUri = MutableLiveData<List<Uri>?>(null)
    //val imageUri: LiveData<List<Uri>?> = _imageUri
    //Imagen
    //private val _imageBitmap = MutableLiveData<List<Bitmap>?>(null)
    //val imageBitmap: LiveData<List<Bitmap>?> = _imageBitmap

    //Control de permisos (creo que no se usa ahora)
    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted
    fun permissionGrand(setGranted: Boolean) {
        _locationPermissionGranted.value = setGranted
    }


    //al inicio del viewmodel
    init {
        //seteo de variables para evitar nullPointerExeption
        _validateName.value = true
        _validateDescription.value = true
        _validateAccessibility.value = true
        _validateDifficulties.value = true
        _validateType.value = true
        _validateRate.value = true
        _isGPSOn.value = false
        _showDialog.value = false
        //Se llama a la ubicacion del usuario
        startLocationUpdates()
    }

    //Actualizacion de campos del formulario con el Livedata
    fun onFieldsChanged(name: String, description: String) {
        _flag.value = false
        _name.value = name
        _description.value = description
    }

    fun onChooserChanged(accessibilityes: String, difficulties: String) {
        _accessibility.value = accessibilityes
        _difficulties.value = difficulties
    }

    //validacion de que los campos no esten vacios
    fun validateDataMakeSuggestion(
        name: String,
        description: String,
        placeType: String,
        rate: Int,
        accessibility: String,
        difficulties: String
    ): Boolean {
        _validateName.value = name.isNotEmpty()
        _validateDescription.value = description.isNotEmpty()
        _validateAccessibility.value = accessibility.isNotEmpty()
        _validateDifficulties.value = difficulties.isNotEmpty()
        _validateType.value = placeType != "Seleccione"
        _validateRate.value = (rate in 1..5)
        return _validateName.value!! && _validateDescription.value!! && _validateAccessibility.value!! && _validateDifficulties.value!! && _validateType.value!! && _validateRate.value!!
    }

    //Almacenar la sugerencia en la bd firestore
    suspend fun makeSuggestion(
        name: String,
        description: String,
        accessibility: String,
        difficulties: String,
        rate: Int,
        placetype: String,
        marker: LatLng,
        user: String
    ) {
        _showDialog.value = false
        _suggestionFlow.value = Resource.Loading
        val suggestion = Suggestion(
            suggestionName = name,
            suggestionDescription = description,
            suggestionAccessibility = accessibility,
            suggestionDifficulties = difficulties,
            suggestionRate = rate,
            suggestionType = placetype,
            suggestionLat = marker.latitude.toString(),
            suggestionLng = marker.longitude.toString(),
            suggestionApproveStatus =  1,
            suggestionAddedBy = user
        )
        _flag.value = true
        val result = db.storeSuggestion(suggestion)
        val key = result.keys.first()
        if (key == Resource.Success("Success")) {
            saveImages(result[key])
        }
        _suggestionFlow.value = key
    }


    //Limpiar campos y reiniciar variables
    fun cleanSuggestionFields() {
        _name.value = ""
        _description.value = ""
        _accessibility.value = ""
        _difficulties.value = ""
        _rating.value = 0
        _placeType.value = "Seleccione"
        _validateName.value = true
        _validateDescription.value = true
        _validateAccessibility.value = true
        _validateDifficulties.value = true
        _validateType.value = true
        _validateRate.value = true
        _flag.value = false
        _showDialog.value = false
        //_imageUri.value = null
        //_imageBitmap.value = null
        imageByteArray = arrayListOf()
        resetImages()

    }

    //Colocar el marcador en el mapa con el Livedata
    fun setMarker(markerLocation: LatLng?) {
        if (markerLocation != null) {
            _markerLocation.value = markerLocation!!
        }
    }

    fun setInitialMarker(userLocation: LatLng?) {
        if (userLocation != null) {
            _markerLocation.value = userLocation!!
        } else {
            Log.d("Location", "Ubicacion desactivada")
        }

    }

    fun setRating(rate: Int) {
        _rating.value = rate
    }

    fun setPlaceType(type: String) {
        _placeType.value = type
    }

    fun setGPSStatus(status: Boolean) {
        _isGPSOn.value = status
    }

    fun setShowDialogTrue() {
        _showDialog.value = true
    }

    fun setShowDialogFalse() {
        _showDialog.value = false
    }


//A partir de aca to do es de las imagenes

    //Imagen
    private var imageByteArray: MutableList<ByteArray>? = arrayListOf()

    fun isImageByteArrayEmpty(): Boolean {
        return imageByteArray?.isEmpty() ?: true
    }

    //Funcion de guardado de imágenes
    private suspend fun saveImages(placeId: String?) {
        if (!isImageByteArrayEmpty() && placeId != null) {
            Log.d("Cantidad", imageByteArray!!.size.toString())
            db.storeImages(imageByteArray!!, placeId)
        } else {
            Log.d("ERROR", "Error inesperado ${!isImageByteArrayEmpty()} y ${placeId.toString()}")
        }
    }

    var state by mutableStateOf(ImageList())
        private set


    fun updateSelectedImageList(listOfImages: List<Uri>, context: Context) {
        //Guardar las imagenes para enviarlas luego a la db
        val resizedImagesByteArray = resizeImages(listOfImages, context)
        for (item in resizedImagesByteArray) {
            if ((imageByteArray?.size ?: 0) < 4) {
                imageByteArray?.add(item)
            } else {
                break
            }
        }
        //Actualizar la UI
        val updatedImageList = state.listOfSelectedImages.toMutableList()
        viewModelScope.launch {
            updatedImageList += listOfImages
            state = state.copy(
                listOfSelectedImages = updatedImageList.distinct()
            )
        }
    }

    fun onItemRemove(index: Int) {
        val updatedImageList = state.listOfSelectedImages.toMutableList()
        viewModelScope.launch {
            updatedImageList.removeAt(index)
            state = state.copy(
                listOfSelectedImages = updatedImageList.distinct()
            )
            imageByteArray?.removeAt(index)
        }
    }

    private fun resetImages() {
        viewModelScope.launch {
            state = state.copy(
                listOfSelectedImages = emptyList()
            )
        }
    }

    private fun resizeImages(listOfImages: List<Uri>, context: Context): MutableList<ByteArray> {
        val PREFERRED_IMAGE_SIZE = 400  //400kb
        val ONE_MB_TO_KB = 1024
        val bitmapImages: MutableList<Bitmap> = arrayListOf()
        val byteArray: MutableList<ByteArray> = arrayListOf()
        val resizedImages: MutableList<Bitmap> = arrayListOf()
        for (item in listOfImages) {
            bitmapImages.add(toBitmap(item, context))
        }
        for (item in bitmapImages) {
            val baos = ByteArrayOutputStream()
            item.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            //if compressed picture is greater than 400kb, than to reduce size
            if (item.byteCount / ONE_MB_TO_KB > PREFERRED_IMAGE_SIZE) {
                //resize photo
                resizedImages.add(resizePhoto(item))
            } else {
                resizedImages.add(item)
            }
            byteArray.add(baos.toByteArray())
        }
        return byteArray
    }

    private fun resizePhoto(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val aspRat = w.toDouble() / h
        val W = 400
        val H = (W * aspRat).toInt()
        val b = Bitmap.createScaledBitmap(bitmap, W, H, false)
        return b
    }


    private fun toBitmap(image: Uri, context: Context): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                image
            )
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, image)
            ImageDecoder.decodeBitmap(source)
        }
        return bitmap
    }

}