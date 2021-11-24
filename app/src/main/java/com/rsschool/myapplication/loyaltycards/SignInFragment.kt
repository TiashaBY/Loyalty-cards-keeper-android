package com.rsschool.myapplication.loyaltycards

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.rsschool.myapplication.loyaltycards.databinding.SignInFragmentBinding
import com.rsschool.myapplication.loyaltycards.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import android.content.Intent
import android.util.Log
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.rsschool.myapplication.loyaltycards.viewmodel.AuthentificationState
import kotlinx.coroutines.flow.collect


class SignInFragment : Fragment() {

    private var _binding : SignInFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val userAuthViewModel by viewModels<AuthViewModel>()
    private lateinit var navController: NavController

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignInFragmentBinding.inflate(inflater, container, false)
        binding.signInButton.setOnClickListener {launchSignInFlow()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.mainFragment, false)
        }

        lifecycleScope.launchWhenCreated {
            userAuthViewModel.authState.collect { state ->
                when (state) {
                    AuthentificationState.AUTH -> {
                        navController.popBackStack()
                        navController.navigate(R.id.cardsDashboardFragment)
                    }
                    AuthentificationState.NOT_AUTH -> {
                        Snackbar.make(view, "Not logged in", Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        Log.w("TAG", "signInResult:failed")
                        Snackbar.make(view, "Error", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun launchSignInFlow() {

        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    "TAG",
                    "Successfully signed in user "
                )
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Snackbar.make(binding.root, "Not logged in", Snackbar.LENGTH_LONG).show()
                Log.i("TAG", "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
/*
    private fun launchSignInFlow() {
        with(binding) {
            signInButton.setOnClickListener {
                val googleSignInOptions =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build()
                googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
                val signInIntent: Intent = googleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, SIGN_IN_RESULT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN_RESULT) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
           // updateUI(account)
            Log.w("TAG", "signInResult:success")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed api code=" + e.message)
           // updateUI(null)
        }
    }*/

    companion object {
        const val SIGN_IN_RESULT = 1001
    }
}
