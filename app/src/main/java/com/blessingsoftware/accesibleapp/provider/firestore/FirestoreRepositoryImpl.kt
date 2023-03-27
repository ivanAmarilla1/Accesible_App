package com.blessingsoftware.accesibleapp.provider.firestore

import android.net.Uri
import android.util.Log
import com.blessingsoftware.accesibleapp.model.domain.*
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
        try {
            db.collection("users").document(uid).set(
                hashMapOf(
                    "email" to email,
                    "name" to name,
                    "provider" to provider,
                    "admin" to false,
                    "added" to FieldValue.serverTimestamp()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun checkUser(id: String): User? {
        val docRef = db.collection("users").document(id)
        val document = docRef.get().await()
        //Log.d("Usuario bd", document.toString())
        return document.toObject<User>()
    }

    override suspend fun addUserPlaceRate(
        uid: String,
        placeId: String,
        rate: Double
    ): Resource<String> {
        return try {
            val ref =
                db.collection("users").document(uid).collection("userPlaceRates").document(placeId)
            ref.set(
                hashMapOf(
                    "placeRate" to rate,
                )
            )
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun checkUserPlaceRate(
        uid: String,
        placeId: String
    ): HashMap<Resource<String>, Int> {
        val result = HashMap<Resource<String>, Int>()
        return try {
            val ref =
                db.collection("users").document(uid).collection("userPlaceRates").document(placeId)
            val x = ref.get().await()
            if (x.exists()) {
                val userPlaceRate = x.toObject(UserPlaceRate::class.java)
                if (userPlaceRate != null) {
                    result[Resource.Success("Exist")] = userPlaceRate.placeRate
                } else {
                    result[Resource.Success("DontExist")] = 0
                }
            } else {
                result[Resource.Success("DontExist")] = 0
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            result[Resource.Failure(e)] = 0
            result
        }
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
        return try {

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
            places
        } catch (e: Exception) {
            e.printStackTrace()
            places
        }

    }

    //TODO GETTER PARA UN PLACE ESPECIFICO
    override suspend fun getSelectedPlace(id: String): Place {
        val places = Place()
        //val ref = db.collection("places").whereEqualTo(id)
        return places

    }

    override suspend fun getPlaces(key: String, value: String): ArrayList<Place> {
        val places = ArrayList<Place>()
        return try {
            val ref = db.collection("places").whereEqualTo(key, value)

            val results = ref.get().await()

            for (document in results) {
                val place = document.toObject(Place::class.java)
                place?.let {
                    //suggestion.suggestionId = document.id
                    places.add(it)
                }
            }
            places
        } catch (e: Exception) {
            e.printStackTrace()
            places
        }

    }

    override suspend fun addPlaceRate(
        placeId: String,
        actualRate: Double,
        actualPlaceNumberOfRaters: Int,
        rate: Int,
        addNumberOfRaters: Int
    ): Resource<String> {

        val newRate = (actualRate + rate)

        return try {
            db.collection("places").document(placeId).update(
                hashMapOf(
                    "placeRate" to newRate,
                    "placeNumberOfRaters" to actualPlaceNumberOfRaters + addNumberOfRaters,
                ) as Map<String, Any>
            ).await()
            Resource.Success("Success")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
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
        return try {
            val ref = db.collection("suggestions").whereEqualTo(key, value)

            val results = ref.get().await()

            for (document in results) {
                val suggestion = document.toObject(Suggestion::class.java)
                suggestion?.let {
                    //suggestion.suggestionId = document.id
                    suggestions.add(it)
                }
            }
            suggestions
        } catch (e: Exception) {
            e.printStackTrace()
            suggestions
        }
    }

    override suspend fun getAllSuggestions(): ArrayList<Suggestion> {
        //Inicializa una lista de objetos tipo Place en la que se guardaran todos los lugares
        val suggestions = ArrayList<Suggestion>()

        return try {
            val ref = db.collection("suggestions")

            val results = ref.get().await()

            for (document in results) {
                val suggestion = document.toObject(Suggestion::class.java)
                suggestion?.let {
                    //suggestion.suggestionId = document.id
                    suggestions.add(it)
                }
            }
            suggestions
        } catch (e: Exception) {
            e.printStackTrace()
            suggestions
        }
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