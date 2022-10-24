package com.blessingsoftware.accesibleapp.provider.firestore

import com.blessingsoftware.accesibleapp.model.domain.Place

interface FirestoreRepository {
    fun storeUser(email: String, name: String, provider: String)
    fun getAllPlaces() : ArrayList<Place>
}