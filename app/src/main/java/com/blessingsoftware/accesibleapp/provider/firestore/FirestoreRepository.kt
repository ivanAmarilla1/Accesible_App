package com.blessingsoftware.accesibleapp.provider.firestore

import android.graphics.Bitmap
import android.net.Uri
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.model.domain.User

interface FirestoreRepository {
    //Users
    fun storeUser(uid: String, email: String, name: String, provider: String)
    suspend fun checkUser(id: String) : User?
    suspend fun addUserPlaceRate(uid: String, placeId: String, rate: Double) : Resource<String>
    suspend fun checkUserPlaceRate(uid: String, placeId: String) : HashMap<Resource<String>, Int>

    //Places
    suspend fun storePlace(place: Place) : Resource<String>
    fun getAllPlaces() : ArrayList<Place>
    suspend fun getPlaces(key: String, value: String) : ArrayList<Place>
    suspend fun addPlaceRate (placeId: String, actualRate: Double, actualPlaceNumberOfRaters: Int, rate: Int, addNumberOfRaters: Int) : Resource<String>

    //Suggestions
    suspend fun storeSuggestion(suggestion: Suggestion) : HashMap<Resource<String>, String>//Resource<String>//, String
    suspend fun deleteSuggestion(uid: String) : Resource<String>
    suspend fun updateSuggestion(suggestion: Suggestion, reviewer: String) : Resource<String>
    suspend fun getSuggestions(key: String, value: Int) : ArrayList<Suggestion>
    suspend fun getAllSuggestions() : ArrayList<Suggestion>

    //Images
    suspend fun storeImages(imgList: List<ByteArray>, placeId: String) : Resource<String>
    suspend fun getImages(placeId: String) : ArrayList<Uri>?
    suspend fun deleteImages(placeId: String, paths: ArrayList<String>) : Resource<String>



}