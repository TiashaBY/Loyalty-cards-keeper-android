package com.rsschool.myapplication.loyaltycards.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class AuthentificationState {
    AUTH, NOT_AUTH, INVALID
}

@HiltViewModel
class AuthViewModel @Inject constructor(userRepository : UserRepository): ViewModel() {

    val authState = userRepository.authUser.map { user ->
        if (user != null) {
            AuthentificationState.AUTH
        } else {
            AuthentificationState.NOT_AUTH
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
