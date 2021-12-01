package com.rsschool.myapplication.loyaltycards.datasource.repository

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val mAuth = FirebaseAuth.getInstance()

    private var _authUser = MutableStateFlow(mAuth.currentUser)
    var authUser : StateFlow<FirebaseUser?> = _authUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener {
            mAuth -> _authUser.value = mAuth.currentUser
    }

    init {
        mAuth.addAuthStateListener(authStateListener)
    }

    fun logOut() {
        mAuth.signOut()
        _authUser.value = null
    }
}
