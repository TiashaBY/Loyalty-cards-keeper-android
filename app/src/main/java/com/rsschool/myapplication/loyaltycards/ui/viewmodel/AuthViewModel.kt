package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.datasource.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
