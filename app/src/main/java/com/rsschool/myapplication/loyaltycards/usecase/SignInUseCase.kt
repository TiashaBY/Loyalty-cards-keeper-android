package com.rsschool.myapplication.loyaltycards.usecase

import android.app.Activity
import android.util.Log
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.rsschool.myapplication.loyaltycards.datasource.repository.AuthRepository
import com.rsschool.myapplication.loyaltycards.datasource.repository.UserPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val prefs: UserPrefsRepository
) {
    operator fun invoke(result: FirebaseAuthUIAuthenticationResult) : SignInResult {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            prefs.loggedInUserUid = user?.uid.toString()
            Log.i("Auth", "Successfully signed in user ")
            return SignInResult.Sucess()
        } else {
            prefs.loggedInUserUid = ""
            Log.i("TAG", "Sign in unsuccessful ${response?.error?.errorCode}")
            return SignInResult.Failure()
        }
    }
}

sealed class SignInResult {
    class Sucess : SignInResult()
    class Failure : SignInResult()
}
