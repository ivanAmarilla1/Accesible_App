package com.blessingsoftware.accesibleapp.provider.firestore

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreRepository {

    override fun storeUser(email: String, name: String, provider: String) {
        db.collection("users").document(email).set(
            hashMapOf("name" to name, "provider" to provider)
        )
    }
}