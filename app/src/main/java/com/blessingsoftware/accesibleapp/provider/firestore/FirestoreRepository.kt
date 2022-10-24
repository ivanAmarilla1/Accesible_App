package com.blessingsoftware.accesibleapp.provider.firestore

interface FirestoreRepository {
    fun storeUser(email: String, name: String, provider: String)
}