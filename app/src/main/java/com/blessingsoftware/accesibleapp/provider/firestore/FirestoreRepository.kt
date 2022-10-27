package com.blessingsoftware.accesibleapp.provider.firestore

import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource

interface FirestoreRepository {
    fun storeUser(email: String, name: String, provider: String)
    fun storeSuggestion(name: String, description: String, latitude: String, longitude: String, userSuggestion: String) : Resource<String>
    fun getAllPlaces() : ArrayList<Place>
}