package com.rsschool.myapplication.loyaltycards.repository

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseUserLiveData  {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private var _authUser = MutableStateFlow<FirebaseUser?>(null)
    var authUser : StateFlow<FirebaseUser?> = _authUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _authUser.value = firebaseAuth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}