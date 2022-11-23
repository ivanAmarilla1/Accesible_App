package com.blessingsoftware.accesibleapp.provider.di

import android.content.Context
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepositoryImpl
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepository
import com.blessingsoftware.accesibleapp.provider.firestore.FirestoreRepositoryImpl
import com.blessingsoftware.accesibleapp.provider.userDatastore.DataStoreRepository
import com.blessingsoftware.accesibleapp.provider.userDatastore.DataStoreRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Inyeccion de dependencias, se usa para enviar dependencia de los repositorios a los ViewModels y tambien las instancias de las BD.
@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: FirebaseAuthRepositoryImpl): FirebaseAuthRepository = impl

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideFirestoreRepository(impl: FirestoreRepositoryImpl): FirestoreRepository = impl

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository = DataStoreRepositoryImpl(context)

}