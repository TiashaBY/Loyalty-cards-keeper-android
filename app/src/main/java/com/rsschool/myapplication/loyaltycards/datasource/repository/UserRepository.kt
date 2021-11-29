package com.rsschool.myapplication.loyaltycards.datasource.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    val mAuth = FirebaseAuth.getInstance()

    private var _authUser = MutableStateFlow<FirebaseUser?>(mAuth.currentUser)
    var authUser : StateFlow<FirebaseUser?> = _authUser.asStateFlow()

    val authStateListener = FirebaseAuth.AuthStateListener {
            mAuth -> _authUser.value = mAuth.currentUser
    }

    init {
        mAuth.addAuthStateListener(authStateListener)
    }
}