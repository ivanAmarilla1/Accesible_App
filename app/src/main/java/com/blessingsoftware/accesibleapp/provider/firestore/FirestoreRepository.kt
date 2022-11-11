package com.blessingsoftware.accesibleapp.provider.firestore

import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion

interface FirestoreRepository {
    fun storeUser(email: String, name: String, provider: String)
    suspend fun storePlace(place: Place) : Resource<String>
    suspend fun storeSuggestion(suggestion: Suggestion) : Resource<String>
    suspend fun updateSuggestion(suggestion: Suggestion, reviewer: String) : Resource<String>
    suspend fun getAllSuggestions() : ArrayList<Suggestion>
    fun getAllPlaces() : ArrayList<Place>
}