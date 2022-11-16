package com.blessingsoftware.accesibleapp.provider.userDatastore

interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getString(key: String) : String?
    suspend fun getBoolean(key: String) : Boolean?
    suspend fun clearPreferences(key: String)
}
