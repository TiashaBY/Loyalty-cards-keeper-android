package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.snackbar.Snackbar
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.SignInFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AuthViewModel
import com.rsschool.myapplication.loyaltycards.usecase.AuthentificationState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: SignInFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val userAuthViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignInFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val signInResultLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        userAuthViewModel.onSignInResult(res)
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
            providers
        ).build()

        signInResultLauncher.launch(signInIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            launchSignInFlow()
        }

        lifecycleScope.launchWhenStarted {
            userAuthViewModel.authState.collect { state ->
                Log.d("auth", "stater!!!=" + state)
                when (state) {
                    AuthentificationState.AUTH -> {
                        Log.d("auth", "navigate to dashboard")
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    AuthentificationState.NOT_AUTH -> {
                        Log.d("auth", "navigate to sign in")
                    }
                }
            }
        }
    }
}

