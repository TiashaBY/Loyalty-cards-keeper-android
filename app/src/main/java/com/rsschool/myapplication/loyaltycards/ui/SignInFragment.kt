package com.rsschool.myapplication.loyaltycards.ui


import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rsschool.myapplication.loyaltycards.databinding.SignInFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AuthViewModel
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AuthentificationState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding : SignInFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val userAuthViewModel by viewModels<AuthViewModel>()

    private val signInResultLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

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

        lifecycleScope.launchWhenStarted {
            userAuthViewModel.authState.collect { state ->
                Log.d("dfghfhgfhgfhhg", "stater!!!=" + state)
                when (state) {
                    AuthentificationState.AUTH -> {
                        Log.d("dfghfhgfhgfhhg", "navigate to dashboard")
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    AuthentificationState.NOT_AUTH -> {
                        Log.d("dfghfhgfhgfhhg", "navigate to sign in")
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
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
            providers
        ).build()

        signInResultLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.i(
                "TAG",
                "Successfully signed in user "
            )
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Snackbar.make(binding.root, "Not logged in", Snackbar.LENGTH_LONG).show()
            Log.i("TAG", "Sign in unsuccessful ${response?.error?.errorCode}")
        }
    }
}

