package com.rsschool.myapplication.loyaltycards.usecase

import com.rsschool.myapplication.loyaltycards.datasource.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetUserAuthStateUseCase @Inject constructor(
    repo: AuthRepository,
) {
    val _authState = MutableStateFlow(
        if (repo.authUser.value != null) {
            AuthentificationState.AUTH
        } else {
            AuthentificationState.NOT_AUTH
        }
    )
    operator fun invoke() =_authState.asStateFlow()
}

enum class AuthentificationState {
    AUTH, NOT_AUTH
}