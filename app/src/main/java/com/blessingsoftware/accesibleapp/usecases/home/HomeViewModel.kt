package com.blessingsoftware.accesibleapp.usecases.home


import androidx.lifecycle.ViewModel
import com.blessingsoftware.accesibleapp.provider.firebase.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: FirebaseAuthRepository) :
    ViewModel() {



}


