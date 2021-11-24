package com.rsschool.myapplication.loyaltycards.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.rsschool.myapplication.loyaltycards.repository.FirebaseUserLiveData
import com.rsschool.myapplication.loyaltycards.repository.UserRepository
import kotlinx.coroutines.flow.*


enum class AuthentificationState {
    AUTH, NOT_AUTH, INVALID
}

class AuthViewModel: ViewModel() {

    val authState = UserRepository().authUser.map { user ->
        Log.d("dfghfhgfhgfhhg", "user!!!=" + user)
        if (user != null) {
            AuthentificationState.AUTH
        } else {
            AuthentificationState.NOT_AUTH
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
