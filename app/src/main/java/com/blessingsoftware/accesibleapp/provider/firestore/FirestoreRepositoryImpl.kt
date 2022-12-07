package com.blessingsoftware.accesibleapp.provider.firestore

import android.net.Uri
import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.Place
import com.blessingsoftware.accesibleapp.model.domain.Resource
import com.blessingsoftware.accesibleapp.model.domain.Suggestion
import com.blessingsoftware.accesibleapp.model.domain.User
import com.blessingsoftware.accesibleapp.util.await
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FirestoreRepository {

    //Users
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

    override suspend fun checkUser(id: String): User? {
        val docRef = db.collection("users").document(id)
        val document = docRef.get().await()
        //Log.d("Usuario bd", document.toString())
        return document.toObject<User>()

    }



    //Places
    override suspend fun storePlace(place: Place): Resource<String> {
        return try {
            db.collection("places").add(place).await()
            Resource.Success("Success")
        } catch (e: Exception) {
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

    override suspend fun getPlaces(key: String, value: String): ArrayList<Place> {
        val places = ArrayList<Place>()
        val ref = db.collection("places").whereEqualTo(key, value)

        val results = ref.get().await()

        for (document in results) {
            val suggestion = document.toObject(Place::class.java)
            suggestion?.let {
                //suggestion.suggestionId = document.id
                places.add(it)
            }
        }
        return places
    }



    //Suggestions
    override suspend fun storeSuggestion(
        suggestion: Suggestion
    ): HashMap<Resource<String>, String> {
        val result = HashMap<Resource<String>, String>()
        return try {
            val x = db.collection("suggestions").add(suggestion).await()
            result[Resource.Success("Success")] = x.id
            result

        } catch (e: Exception) {
            e.printStackTrace()
            result[Resource.Failure(e)] = "Failure"
            result
        }
    }

    override suspend fun deleteSuggestion(uid: String): Resource<String> {
        return try {
            db.collection("suggestions").document(uid).delete().await()
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

    override suspend fun getSuggestions(key: String, value: Int): ArrayList<Suggestion> {
        //Inicializa una lista de objetos tipo Place en la que se guardaran todos los lugares
        val suggestions = ArrayList<Suggestion>()
        val ref = db.collection("suggestions").whereEqualTo(key, value)

        val results = ref.get().await()

        for (document in results) {
            val suggestion = document.toObject(Suggestion::class.java)
            suggestion?.let {
                //suggestion.suggestionId = document.id
                suggestions.add(it)
            }
        }
        return suggestions
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
    }



    //Images
    override suspend fun storeImages(imgList: List<ByteArray>, placeId: String): Resource<String> {
        var counter = 0
        return try {
            val folder: StorageReference = storage.reference.child("suggestionImages")
            val folderId: StorageReference = folder.child(placeId)
            for (item in imgList) {
                val fileName: StorageReference = folderId.child("file$counter")
                fileName.putBytes(item).await()
                counter += 1
            }
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getImages(placeId: String): ArrayList<Uri>? {
        val folder: StorageReference = storage.reference.child("suggestionImages")
        val folderId = folder.child(placeId).listAll().await()
        val images = ArrayList<Uri>()
        return try {
            for (item in folderId.items) {
                images.add(item.downloadUrl.await())
            }
            images
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteImages(placeId: String, paths: ArrayList<String>): Resource<String> {
        return try {
            for (item in paths) {
                val imagePath: StorageReference = storage.reference.child(item)
                imagePath.delete().await()
            }
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }



}