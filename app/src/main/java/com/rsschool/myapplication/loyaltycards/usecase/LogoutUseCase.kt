package com.rsschool.myapplication.loyaltycards.usecase

import com.rsschool.myapplication.loyaltycards.datasource.repository.AuthRepository
import com.rsschool.myapplication.loyaltycards.datasource.repository.UserPrefsRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repo: AuthRepository,
    private val prefs: UserPrefsRepository
) {
    operator fun invoke() {
        repo.logOut()
        prefs.loggedInUserUid = ""
    }
}