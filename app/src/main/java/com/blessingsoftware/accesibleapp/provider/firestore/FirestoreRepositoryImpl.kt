package com.blessingsoftware.accesibleapp.provider.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.util.await
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreRepository {

    override fun storeUser(email: String, name: String, provider: String) {
        db.collection("users").document(email).set(
            hashMapOf(
                "name" to name,
                "provider" to provider,
                "timestamp" to FieldValue.serverTimestamp()
            )
        )
    }

    override suspend fun storeSuggestion(
        suggestion: Suggestion
    ): Resource<String> {
        return try {
            db.collection("suggestions").add(suggestion).await()
            Resource.Success("Success")
        }catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun getAllPlaces(): ArrayList<Place> {
        //Inicializa una lista de objetos tipo Place en la que se guardaran todos los lugares
        val places = ArrayList<Place>()

        val ref = db.collection("places")

        ref.addSnapshotListener { snapshot, e ->
            // handle the error if there is one, and then return
            if (e != null) {
                Log.w("Listen failed", e)
                return@addSnapshotListener
            }
            // if we reached this point , there was not an error
            snapshot?.let {
                val documents = snapshot.documents
                documents.forEach {
                    val place = it.toObject(Place::class.java)
                    //Log.d("Place name", place!!.placeName)
                    place?.let {
                        places.add(it)
                    }
                }
            }
        }
        return places
    }
}

/*ref.get().addOnSuccessListener { result ->
            for (document in result) {
                val place = document.toObject<Place>()
                places.add(place)
                Log.d("Place name", place.name)
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }*/