package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.rsschool.myapplication.loyaltycards.usecase.AuthentificationState
import com.rsschool.myapplication.loyaltycards.usecase.GetUserAuthStateUseCase
import com.rsschool.myapplication.loyaltycards.usecase.SignInResult
import com.rsschool.myapplication.loyaltycards.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val signInUseCase: SignInUseCase,
                                        private val userAuthStateUseCase: GetUserAuthStateUseCase
) : ViewModel() {
    val _authState = MutableStateFlow(userAuthStateUseCase.invoke().value)
    val authState = _authState.asStateFlow()


    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when (signInUseCase.invoke(result)) {
            is SignInResult.Sucess ->
                _authState.value = AuthentificationState.AUTH
        else ->
            _authState.value = AuthentificationState.NOT_AUTH
        }
    }
}
