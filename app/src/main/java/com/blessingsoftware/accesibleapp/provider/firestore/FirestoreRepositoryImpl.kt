package com.blessingsoftware.accesibleapp.provider.firestore

import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.model.domain.User
import com.blessingsoftware.accesibleapp.util.await
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirestoreRepository {

    override fun storeUser(uid: String, email: String, name: String, provider: String) {
        db.collection("users").document(uid).set(
            hashMapOf(
                "email" to email,
                "name" to name,
                "provider" to provider,
                "admin" to false,
                "added" to FieldValue.serverTimestamp()
            )
        )
    }

    override suspend fun storePlace(place: Place): Resource<String> {
        return try {
            db.collection("places").add(place).await()
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun storeSuggestion(
        suggestion: Suggestion
    ): Resource<String> {
        return try {
            db.collection("suggestions").add(suggestion).await()
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateSuggestion(
        suggestion: Suggestion,
        reviewer: String
    ): Resource<String> {
        return try {
            db.collection("suggestions").document(suggestion.suggestionId).update(
                hashMapOf(
                    "suggestionApproveStatus" to suggestion.suggestionApproveStatus,
                    "suggestionReviewedBy" to reviewer,
                ) as Map<String, Any>
            ).await()
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }

    }

    override suspend fun getAllSuggestions(): ArrayList<Suggestion> {
        //Inicializa una lista de objetos tipo Place en la que se guardaran todos los lugares
        val suggestions = ArrayList<Suggestion>()
        val ref = db.collection("suggestions")

        val results = ref.get().await()

        for (document in results) {
            val suggestion = document.toObject(Suggestion::class.java)
            suggestion?.let {
                //suggestion.suggestionId = document.id
                suggestions.add(it)
            }
        }
        return suggestions

        /*ref.addSnapshotListener { snapshot, e ->
            // handle the error if there is one, and then return
            if (e != null) {
                Log.w("Listen failed", e)
                return@addSnapshotListener
            }
            // if we reached this point , there was not an error
            snapshot?.let {
                val documents = snapshot.documents
                documents.forEach {
                    val suggestion = it.toObject(Suggestion::class.java)
                    //Log.d("Place name", place!!.placeName)
                    suggestion?.let {
                        suggestions.add(it)
                    }
                }
            }
        }*/
    }

    override suspend fun checkUser(id: String): User? {
        val docRef = db.collection("users").document(id)
        val document = docRef.get().await()
        //Log.d("Usuario bd", document.toString())
        return document.toObject<User>()

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

/*
* ref.get().addOnSuccessListener { result ->
            for (document in result) {
                val place = document.toObject<Place>()
                places.add(place)
                Log.d("Place name", place.name)
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }*/